import os
import sys
from google import genai


global_client: genai.Client | None = None


def get_client() -> genai.Client:
    key = os.environ.get("GEMINI_API_KEY")

    global global_client

    if not key:
        sys.stderr.write("请配置keyn\n")
        sys.exit(1)
    if not global_client:
        global_client = genai.Client(api_key=key)

    return global_client
