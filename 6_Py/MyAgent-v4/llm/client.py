from google import genai
from pathlib import Path
import yaml
import os
import sys

_client: genai.Client | None = None
BASE_DIR = Path(__file__).resolve().parent.parent


def __get_key() -> str | None:

    my_api_key: str | None = os.getenv("GEMINI_API_KEY")
    if not my_api_key or my_api_key.startswith("your_"):
        CONFIG_YAML = BASE_DIR / "config" / "config.yaml"
        with open(CONFIG_YAML, "r", encoding="utf-8") as f:
            config = yaml.safe_load(f)
            my_api_key = config.get("gemini", {}).get("key", "")

    if not my_api_key or my_api_key.startswith("your_"):
        sys.stderr.write("无效的key")
        sys.exit(1)
    return my_api_key


def get_client() -> genai.Client:
    global _client
    if _client is None:
        api_key = __get_key()
        _client = genai.Client(api_key=api_key)
    return _client
