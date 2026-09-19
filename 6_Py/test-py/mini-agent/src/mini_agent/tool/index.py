
from mini_agent.tool.read import ReadFileTool
from mini_agent.tool.base import Tool
from google.genai import types
ALL_TOOLS = [
    ReadFileTool(),
]
tools = {tool.name:tool for tool in ALL_TOOLS}

def get_tool(name:str)->Tool|None:
    return tools.get(name)

def to_function_declaration()->list[types.Tool]:

    descriptions = [tool.get_description() for tool in ALL_TOOLS]
    if descriptions is None:
        return []
    return [types.Tool(function_declarations=descriptions)]
    