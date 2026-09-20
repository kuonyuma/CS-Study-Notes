from fastapi import FastAPI, HTTPException
from pydantic import BaseModel

app = FastAPI(title="Fake DB FastAPI Demo")

# 1. 模拟数据库 (Fake Database: 用内存字典模拟数据表)
fake_users_db = {
    1: {"id": 1, "name": "alice", "role": "admin"},
    2: {"id": 2, "name": "bob", "role": "user"},
}

# 2. 数据模式 (Pydantic Schema: 请求体结构校验)
class UserCreate(BaseModel):
    name: str
    role: str = "user"

# 3. 核心接口与数据库操作
@app.get("/users")
def list_users():
    # 👈 核心机制：查询所有记录并返回
    return list(fake_users_db.values())

@app.get("/users/{user_id}")
def get_user(user_id: int):
    # 👈 核心机制：根据主键查询单条数据
    user = fake_users_db.get(user_id)
    if not user:
        raise HTTPException(status_code=404, detail="User not found")
    return user

@app.post("/users", status_code=201)
def create_user(user: UserCreate):
    # 👈 核心机制：计算自增主键并模拟写入数据库
    new_id = max(fake_users_db.keys(), default=0) + 1
    new_user = {"id": new_id, "name": user.name, "role": user.role}
    fake_users_db[new_id] = new_user
    return new_user

if __name__ == "__main__":
    import uvicorn
    # 直接运行当前脚本即可启动服务
    uvicorn.run("main:app", host="127.0.0.1", port=8000, reload=True)
