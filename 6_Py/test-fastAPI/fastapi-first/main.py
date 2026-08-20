from fastapi import FastAPI, status, HTTPException
from pydantic import BaseModel

app = FastAPI()


class CreateMessageRequest(BaseModel):
    name: str
    user_id: int
    password: str


class CreateMessageResponse(BaseModel):
    name: str
    password: str


@app.get("/index")
async def root():
    return "Hello World"


# 模拟数据库
userDb = {"admin": {"password": "admin", "user_id": 1}}


@app.post(
    "/register",
    status_code=status.HTTP_201_CREATED,
    response_model=CreateMessageResponse,
)
async def create_message(
    body: CreateMessageRequest,
) -> CreateMessageResponse:

    if body.name in userDb:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="User already exists",
        )
    new_user_id = len(userDb) + 1
    userDb[body.name] = {
        "password": body.password,
        "user_id": new_user_id,
    }

    return CreateMessageResponse(name=body.name, password=body.password)
