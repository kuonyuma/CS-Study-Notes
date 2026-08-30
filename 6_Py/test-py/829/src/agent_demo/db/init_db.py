import asyncio

from . import models  # noqa: F401 - registers the ORM models with Base.metadata
from .base import Base
from .engine import engine


async def init_db() -> None:
    async with engine.begin() as connection:
        await connection.run_sync(Base.metadata.create_all)
    await engine.dispose()


if __name__ == "__main__":
    asyncio.run(init_db())
