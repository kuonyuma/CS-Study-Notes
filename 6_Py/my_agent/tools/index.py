from tools.list_files import ListFilesTool
from tools.read_file import ReadFileTool
from tools.write_file import WriteFileTool
from tools.edit_file import EditFileTool
from tools.yaml_loader import LoadYamlTool
from tools.run_command import RunCommandTool
from google.genai import types
from tools.base import Tool

ALL_TOOLS: list[Tool] = [
    ListFilesTool(),
    ReadFileTool(),
    WriteFileTool(),
    EditFileTool(),
    LoadYamlTool(),
    RunCommandTool(),
]


def find_tool(name: str) -> Tool | None:
    for i in ALL_TOOLS:
        if i.name == name:
            return i
    return None


def get_function_declarations() -> list[types.Tool]:
    declarations = [i.to_function_declaration() for i in ALL_TOOLS]
    return [types.Tool(function_declarations=declarations)]
