from typing import List, Any
from google.genai import types
from .tools.index import find_tool

async def execute_tools(function_calls: List[Any]) -> types.Content:
    """
    Execute tool calls sequentially and build the single user Content
    that carries every FunctionResponse back to Gemini.
    """
    response_parts = []

    for fc in function_calls:
        name = fc.name
        args = dict(fc.args) if fc.args else {}
        tool = find_tool(name)

        if not tool:
            result_content = f"Error: Tool '{name}' not found."
        else:
            try:
                res = await tool.run(args)
                result_content = res.content
            except Exception as err:
                result_content = f"Error executing tool '{name}': {err}"

        # Build function response part for Gemini
        part = types.Part.from_function_response(
            name=name,
            response={"result": result_content}
        )
        response_parts.append(part)

    return types.Content(role="user", parts=response_parts)
