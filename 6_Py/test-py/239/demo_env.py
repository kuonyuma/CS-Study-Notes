import os
from dotenv import load_dotenv
from pydantic_settings import BaseSettings, SettingsConfigDict

# ============================================================
# 方式一：原生用法 (python-dotenv)
# ============================================================
# 👈 核心机制：读取 .env 文件并注入到系统环境变量中 (os.environ)
load_dotenv()

app_name = os.getenv("APP_NAME")
port = os.getenv("PORT")       # 👈 注意：os.getenv 返回的都是字符串 str
debug = os.getenv("DEBUG")

print("--- [方式一] 原生 dotenv 读取 ---")
print(f"APP_NAME : {app_name} (类型: {type(app_name).__name__})")
print(f"PORT     : {port} (类型: {type(port).__name__})")
print(f"DEBUG    : {debug} (类型: {type(debug).__name__})")


# ============================================================
# 方式二：现代工程推荐 (pydantic-settings，你的项目中已安装)
# ============================================================
# 👈 核心机制：定义类型模型，自动将 .env 的文本转为对应的 Python 类型 (int, bool 等)
class Settings(BaseSettings):
    app_name: str
    port: int = 8000
    debug: bool = False
    database_url: str
    secret_key: str

    # 指定读取当前目录下的 .env
    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        extra="ignore",
    )

settings = Settings()

print("\n--- [方式二] Pydantic Settings 强类型读取 ---")
print(f"settings.app_name     : {settings.app_name}")
print(f"settings.port         : {settings.port} (类型: {type(settings.port).__name__})")
print(f"settings.debug        : {settings.debug} (类型: {type(settings.debug).__name__})")
print(f"settings.database_url : {settings.database_url}")
