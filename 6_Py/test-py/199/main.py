
from fastapi import FastAPI
from pydantic import BaseModel

app = FastAPI()

class User(BaseModel):
    name:str
    age:int
    email:str|None = None

db_fake:dict[int,User] = {}


# create
@app.post("/user/{id}")
async def add(t:User,id:int):
    db_fake[id] = t
    return "OK"

@app.get("/user/{id}")
async def get(id:int):
    return db_fake[id]
