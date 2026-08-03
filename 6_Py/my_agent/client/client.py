import os
import sys
from google.genai import types
from google import genai

client: genai.Client | None = None


def get_client():
    global client
    if client is not None:
        return client
    gemini_api_key = os.getenv(key="GEMINI_API_KEY", default="")
    if gemini_api_key == "":
        sys.stderr.write("请配置key")
        sys.exit(1)

    client = genai.Client(api_key=gemini_api_key)
    return client
