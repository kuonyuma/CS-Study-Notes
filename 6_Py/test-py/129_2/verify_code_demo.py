import random
import redis

r = redis.Redis("127.0.0.1",port=6380,decode_responses=True)

def set_code(phone:str):
    code = random.randint(1,100)
    key = f"id:{phone}"
    r.set(key,value=code)
    return code

def verify_code(phone:str,code:int):
    key = f"id:{phone}"
    tmp = r.get(key)

    if code == int(tmp):
        print("验证成功")
    else:
        print("验证失败")


def main():
    code =set_code("iqoo9")
    verify_code("iqoo9",code)

if __name__ == "__main__":
    main()