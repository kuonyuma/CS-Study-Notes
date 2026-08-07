import os
import sys
from google import genai
from config.settings import settings

client: genai.Client | None = None


def get_client():
    global client
    if client is not None:
        return client
    gemini_api_key = os.getenv(key="GEMINI_API_KEY", default="")
    gemini_api_key = settings.model_config.key
    if gemini_api_key == "" or gemini_api_key.startswith("your"):
        sys.stderr.write("请配置key")
        sys.exit(1)

    client = genai.Client(api_key=gemini_api_key)
    return client
