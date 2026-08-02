from typing import List, Dict, Optional
from google.genai import types
from .base import Tool
from .list_files import ListFilesTool
from .read_file import ReadFileTool

ALL_TOOLS: List[Tool] = [
    ListFilesTool(),
    ReadFileTool(),
]

def find_tool(name: str) -> Optional[Tool]:
    for t in ALL_TOOLS:
        if t.name == name:
            return t
    return None

def get_gemini_tools() -> List[types.Tool]:
    declarations = [t.to_gemini_declaration() for t in ALL_TOOLS]
    return [types.Tool(function_declarations=declarations)]
