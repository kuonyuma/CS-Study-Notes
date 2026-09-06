
from typing import ClassVar
class DatabaseConfig:
    # 显式声明为类变量：所有实例共享
    default_timeout: ClassVar[int] = 30
    
    # 普通类型注解：实例变量
    host: str
    port: int

    def __init__(self, host: str, port: int):
        self.host = host
        self.port = port

bean = DatabaseConfig("locahost",8080)

print(bean.default_timeout)
print(bean.host)

DatabaseConfig.default_timeout = 1
print(DatabaseConfig.default_timeout)