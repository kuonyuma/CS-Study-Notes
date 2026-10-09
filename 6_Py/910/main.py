"""阅读顺序：lifespan → get_session → CRUD。Python 3.11+。"""

import logging
import os
from collections.abc import AsyncIterator
from contextlib import asynccontextmanager
from typing import Annotated

from fastapi import Depends, FastAPI, HTTPException, Request, Response
from pydantic import BaseModel, ConfigDict, Field
from redis.asyncio import Redis
from sqlalchemy import String, select
from sqlalchemy.ext.asyncio import AsyncSession, async_sessionmaker, create_async_engine
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger("library")


class Base(DeclarativeBase):
    pass


class Book(Base):
    __tablename__ = "books"

    id: Mapped[int] = mapped_column(primary_key=True)
    title: Mapped[str] = mapped_column(String(200))
    author: Mapped[str] = mapped_column(String(100))


class BookInput(BaseModel):
    title: str = Field(min_length=1, max_length=200)
    author: str = Field(min_length=1, max_length=100)


class BookOutput(BookInput):
    model_config = ConfigDict(from_attributes=True)
    id: int


@asynccontextmanager
async def lifespan(app: FastAPI):
    # 应用级资源：每个进程一套连接池和会话工厂，不在每次请求中创建。
    engine = create_async_engine(
        os.getenv("DATABASE_URL", "mysql+aiomysql://demo:demo_pass@127.0.0.1:3307/library?charset=utf8mb4"),
        pool_pre_ping=True,
        echo=os.getenv("SQL_ECHO", "0") == "1",
    )
    
    try:
        redis = Redis.from_url(
            os.getenv("REDIS_URL", "redis://127.0.0.1:6380/0"),
            decode_responses=True,
        )
        try:
            # 构造客户端不等于连接成功；执行命令才能验证外部服务。
            async with engine.begin() as connection:
                await connection.run_sync(Base.metadata.create_all)
            await redis.ping()
            app.state.session_factory = async_sessionmaker(engine, expire_on_commit=False)
            app.state.redis = redis
            logger.info("STARTUP: MySQL / Redis 就绪，开始接收请求")
            yield  # 应用在这里运行；关闭时继续执行 finally。
        finally:
            await redis.aclose()
            logger.info("SHUTDOWN: Redis 连接池已关闭")
    finally:
        # 即使启动中途失败，也清理已创建的资源。
        await engine.dispose()
        logger.info("SHUTDOWN: MySQL 连接池已释放")


app = FastAPI(title="图书管理 · Lifespan Demo", lifespan=lifespan)


async def get_session(request: Request) -> AsyncIterator[AsyncSession]:
    # 请求级资源：工厂共享，Session 独立；退出时关闭会话并归还连接。
    async with request.app.state.session_factory() as session:
        logger.info("SESSION: 打开请求会话")
        try:
            yield session
        finally:
            logger.info("SESSION: 请求结束，即将关闭会话（未提交事务会回滚）")


def get_redis(request: Request) -> Redis:
    # 这里借用应用级客户端，不在请求结束时关闭它。
    return request.app.state.redis


SessionDep = Annotated[AsyncSession, Depends(get_session)]
RedisDep = Annotated[Redis, Depends(get_redis)]


async def find_book(book_id: int, session: AsyncSession) -> Book:
    book = await session.get(Book, book_id)
    if book is None:
        raise HTTPException(status_code=404, detail="图书不存在")
    return book


@app.post("/books", response_model=BookOutput, status_code=201)
async def create_book(data: BookInput, session: SessionDep):
    book = Book(**data.model_dump())
    session.add(book)
    await session.commit()  # 写操作显式提交，成功后才返回。
    return book


@app.get("/books", response_model=list[BookOutput])
async def list_books(session: SessionDep):
    return (await session.scalars(select(Book).order_by(Book.id))).all()


@app.get("/books/{book_id}", response_model=BookOutput)
async def read_book(book_id: int, response: Response, session: SessionDep, redis: RedisDep):
    key = f"book:{book_id}"
    cached = await redis.get(key)
    if cached is not None:
        response.headers["X-Cache"] = "HIT"
        return BookOutput.model_validate_json(cached)
    book = BookOutput.model_validate(await find_book(book_id, session))
    await redis.set(key, book.model_dump_json(), ex=60)
    response.headers["X-Cache"] = "MISS"
    return book


@app.put("/books/{book_id}", response_model=BookOutput)
async def update_book(book_id: int, data: BookInput, session: SessionDep, redis: RedisDep):
    book = await find_book(book_id, session)
    book.title, book.author = data.title, data.author
    await session.commit()
    await redis.delete(f"book:{book_id}")
    return book


@app.delete("/books/{book_id}", status_code=204)
async def delete_book(book_id: int, session: SessionDep, redis: RedisDep):
    await session.delete(await find_book(book_id, session))
    await session.commit()
    await redis.delete(f"book:{book_id}")
    return Response(status_code=204)
