
from mini_agent.tool.base import ToolResult,Tool
from mini_agent.tool.index import tools
from google.genai import types
from dataclasses import dataclass
from typing import Any

@dataclass
class ToolExecution:
    name:str
    id:str
    argument:dict[str,Any]
    result:ToolResult

@dataclass
class ToolExecutionBatch:
    content:types.Content
    tool_execution:list[ToolExecution]

def tool_result_part(result:ToolResult,name:str,id:str,)->types.Part:

    part = types.Part.from_function_response(
        name=name,
        response={
            "result":result.content,
            "error":result.error
        },
    )
    if id and part.function_response is not None:
        part.function_response.id = id
    return part

async def tool_execut(
    calls:list[types.FunctionCall],
    tool_register:dict[str,Any]|None = None,
    allow_tool_ids:frozenset[str] = frozenset(),
)->ToolExecutionBatch:
    
    execut:list[ToolExecution] = []
    tool_parts:list[types.Part] = []
    if tool_register is None:
        tool_register = tools

    for fc in calls:
        name = fc.name or ""
        id = fc.id or ""
        args = dict(fc.args or {})
        t:Tool|None = tool_register.get(name)

        if name == "":
            result = ToolResult(content="工具名称为空字符串无法调用",error=True)
        elif t is None:
            result = ToolResult(content=f"未知工具{name}",error=True)
        elif not t.read_only and id not in allow_tool_ids: 
            result = ToolResult(content=f"无权限调用该工具{name}",error=True)
        else:
            result = await t.run(agument=args)

        part_tmp = tool_result_part(name=name,id=id,result=result)
        tool_parts.append(part_tmp)
        tool_execution_tmp = ToolExecution(name=name,id=id,result=result,argument=args)
        execut.append(tool_execution_tmp)

    return ToolExecutionBatch(
        content=types.Content(role="user",parts=tool_parts),
        tool_execution=execut,
    )





        


