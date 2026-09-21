from fastapi import FastAPI, HTTPException,Header,Cookie
from pydantic import BaseModel
from typing import Annotated

app = FastAPI()
fake_db:dict[int,dict] ={
    1001:{
        "name":"ryuke",
        "age":20
    },
    1002:{
        "name":"ryuke",
        "age":20    
    }
}

class RequestModel(BaseModel):
    name:str
    age:int

class ResponseModel(BaseModel):
    name:str
    age:int

@app.get(
        "/user/{user_id}",
        response_model=ResponseModel
)
async def user_get(user_id:int):
    if user_id not in fake_db:
        raise HTTPException(
            status_code=404,
            detail="user_id not found"
        )
    return fake_db[user_id]

@app.post("/users",status_code=201)
async def create(query:RequestModel):
    user_id = max(fake_db.keys()) + 1
    fake_db[user_id] ={
        "name":query.name,
        "age":query.age
    }

    return f"创建完成 用户id{user_id}"

@app.get("/info")
async def get_info(
    user_agent : Annotated[str|None,Header()] = None,
    cookie:Annotated[str|None,Cookie()] = None,
):
    return {
        "user-agent":user_agent,
        "cookie":cookie
    }

