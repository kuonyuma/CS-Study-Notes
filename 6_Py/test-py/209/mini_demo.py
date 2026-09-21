from fastapi import FastAPI,Depends,HTTPException
from typing import Annotated
from pydantic import BaseModel

app = FastAPI()

fake_db:dict[int,dict] ={
    1001:{
        "name":"ryuke",
        "age":10,
        "password":"114114",
        "email":"ryuke@gmail.com",
    },
    1002:{
            "name":"moka",
            "age":18,
            "password":"121212",
            "email":"moka@gmail.com",
    },
}


class RequestMolde(BaseModel):
    name:str
    age:int
    password:str
    email:str|None =None

class ResponseMolde(BaseModel):
    name:str
    age:int
    email:str|None = None

@app.get("/user/{user_id}",response_model=ResponseMolde)
async def get(user_id:int):
    if user_id not in fake_db.keys():
        raise HTTPException(
            status_code=401,
            detail="not found user"
        )
    return fake_db[user_id]

@app.post("/users",status_code=201)
async def add_user(t:RequestMolde):
    user_id = max(fake_db.keys()) + 1

    fake_db[user_id] = {
        "name":t.name,
        "age":t.age,
        "password":t.password,
        "email":t.email,
    }
    return fake_db[user_id]

async def check(password: str, user_id: int) -> bool:

    if user_id not in fake_db:
        raise HTTPException(
            status_code=404,
            detail="not found",
        )

    return password == fake_db[user_id]["password"]


@app.delete("/user/{user_id}")
async def remove(
    user_id: int,
    check_password: Annotated[bool, Depends(check)],
):
    if not check_password:
        raise HTTPException(
            status_code=400,
            detail="password error",
        )

    fake_db.pop(user_id)

    return "OK"