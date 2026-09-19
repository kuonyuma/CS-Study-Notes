import redis
import json

fake_db = {
    "user:1001":{
        "name":"alice",
        "age":10,
    },
    "user:1002":{
        "name":"ryuke",
        "age":20,
    }
}

r = redis.Redis("127.0.0.1",port=6380,decode_responses=True)

def get_user(id:int):
    key= f"user:{id}"

    tmp = r.get(key)

    if tmp is not None:
        return json.loads(tmp)

    if key in fake_db:
        tmp = fake_db["key"]
        r.set(key,value=json.dumps(tmp),ex= 300)
        return tmp
    print(f"{key}不存在与数据库中")
    