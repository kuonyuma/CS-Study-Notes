from tools.list_files import ListFiles
from google.genai import types
from tools.base import Tool

ALL_TOOL: list[Tool] = [ListFiles()]


def get_tool(name: str) -> Tool | None:
    for i in ALL_TOOL:
        if i.name == name:
            return i
    return None


def get_function_declarations() -> list[types.Tool]:
    declarations = [i.get_tool_message() for i in ALL_TOOL]
    return [types.Tool(function_declarations=declarations)]
