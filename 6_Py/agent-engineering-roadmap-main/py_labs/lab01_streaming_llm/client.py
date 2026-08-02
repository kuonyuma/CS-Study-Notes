import os
import sys
from google import genai

MODEL = "gemini-3.6-flash"
DEFAULT_MAX_TOKENS = 4096

_client_instance: genai.Client | None = None


def get_client() -> genai.Client:
    """
    One shared client for the whole process.
    Fails loudly and early when the API key is missing.
    """
    global _client_instance
    if _client_instance is None:
        api_key = os.environ.get("GEMINI_API_KEY")
        if not api_key:
            sys.stderr.write(
                "Error: GEMINI_API_KEY is not set.\n\n"
                "Get an API key from https://aistudio.google.com/, then run:\n"
                "  export GEMINI_API_KEY=your_api_key_here\n"
            )
            sys.exit(1)
        _client_instance = genai.Client(api_key=api_key)
    return _client_instance
