import os

from google import genai
from mini_agent.config.settings import setting

client:genai.Client|None = None

def get_client()->genai.Client:
    global client
    if client is not None:
        return client
    key = os.getenv("GEMINI_API_KEY",default="").strip()
    if key == "":
        key =setting.model.key

    if key == "":
        raise ValueError("未配置key")
    client = genai.Client(api_key=key)
    return client


async def client_close():
    global client
    current_client = client
    client = None
    if current_client is None:
        return

    try:
        await current_client.aio.aclose()
    finally:
        current_client.close()


