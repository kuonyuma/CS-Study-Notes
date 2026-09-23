from fastapi import FastAPI, status
from pydantic import BaseModel

app = FastAPI()


class UserCreate(BaseModel):
    name: str
    age: int


class UserResponse(BaseModel):
    id: int
    name: str
    age: int


@app.post(
    "/users",
    response_model=UserResponse,
    status_code=status.HTTP_201_CREATED,
)
def create_user(user: UserCreate):
    return {
        "id": 1,
        "name": user.name,
        "age": user.age,
    }

@app.get(
    "/user/{user_id}",
)
async def get_user(user_id:int,name:str):
    return f"user_id{user_id}"