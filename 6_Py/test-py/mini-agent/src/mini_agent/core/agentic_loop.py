from dataclasses import dataclass
from typing import (
    Any,
    Awaitable,
    Literal,
)
from hashlib import sha256
import json
from uuid import uuid7
from collections.abc import Callable, AsyncGenerator, Mapping
from mini_agent.client.ask_gemini import ask_gemini, Model_Result
from google.genai import types
from mini_agent.config.settings import setting
from mini_agent.tool.base import Tool

@dataclass(frozen=True)
class ToolApprovalRequest:
    name: str
    call_id: str
    argument: dict[str, Any]
    hash: str

    @classmethod
    def create(
        cls,
        name: str,
        call_id: str,
        args: dict[str, Any],
    ):
        tmp = json.dumps(
            {
                "name": name,
                "call_id": call_id,
                "argument": args,
            },
            ensure_ascii=False,
            sort_keys=True,
            separators=(",", ":"),
        )
        hash_code = sha256(tmp.encode("utf-8")).hexdigest()
        return cls(
            name=name,
            call_id=call_id,
            argument=dict(args),
            hash=hash_code,
        )


PermissionCheck = Callable[[ToolApprovalRequest], Awaitable[bool]]


@dataclass
class LoopResult:
    full_text: str
    usage: dict[str, Any]
    reason: str
    history: list[types.Content]
    new_content: list[types.Content]
    calls: list[types.FunctionCall]


@dataclass
class LoopEvent:
    type: Literal["text", "tool_start", "done", "error","tool_done"]
    tier: int
    text: str = ""
    result: LoopResult | None = None
    tool_name:str =""
    tool_id :str = ""
    tool_args:str = ""

async def query(
    contents: list[types.Content],
    tool_register: Mapping[str, Any] | None = None,
    tier: int = 10,
    system_prompt: str | None = None,
    permissioncheck: PermissionCheck | None = None,
) -> AsyncGenerator[LoopEvent]:

    beta_contents = list(contents)
    beta_contents_len = len(contents)
    try:
        for i in range(1, tier + 1):
            cur = i
            model_result: Model_Result | None = None
            async for event in ask_gemini(
                contents=beta_contents,
                system_prompt=(
                    system_prompt
                    if system_prompt is not None
                    else setting.system_prompt
                ),
            ):
                if event.type == "text":
                    yield LoopEvent(type="text", text=event.text, tier=cur)
                elif event.type == "done":
                    model_result = event.result

            if model_result is None:
                raise Exception("未收到结果result")

            beta_contents.append(types.Content(role="model", parts=model_result.parts))
            if not model_result.calls:
                yield LoopEvent(
                    type="done",
                    tier=cur,
                    result=LoopResult(
                        full_text=model_result.full_text,
                        usage=model_result.usage,
                        reason=model_result.stop_reason,
                        history=beta_contents,
                        new_content=beta_contents[beta_contents_len:],
                        calls=model_result.calls,
                    ),
                )
                return
            else:
                name_ids:list[tuple[[str, str, Tool, ToolApprovalRequest | None]]] = []
                for fc in model_result.calls:
                    name = fc.name
                    if name is None or name == "":
                        raise Exception("工具名字不可以为空")
                    if fc.id == "":
                        id = str(uuid7())
                    fc.id = id
                    args = fc.args
                    t = tool_register.get(name)

    
                    requires_approval = bool(t is not None and not t.read_only)

                    tool_approvalprequest = ToolApprovalRequest.create(
                        name=name,
                        call_id=id,
                        args=args,
                    ) if requires_approval else None

                    name_ids.append(id,name,t,tool_approvalprequest)
                    yield LoopEvent(
                        type="tool_start",
                        tool_name=name,
                        tool_args=args,
                        tool_id=id,
                    )
                parts:list[types.Part] = []
                for id,name,fc,tool_approvalprequest in name_ids:
                    allow_ids:frozenset[str] = frozenset()
                    if tool_approvalprequest is not None:
                        check = permissioncheck[tool_approvalprequest]
                    if check:
                        allow_ids = frozenset(id)
                    t = tool_register.get(name)
                    if t is not None:
                        batch = await t.run([fc],tool_register,allow_ids)
                    for execution in batch.tool_execution:
                        yield LoopEvent(
                            type="tool_done",
                            tool_name=name,
                            tool_id=id,
                            tool_args=args,
                        )
                    if batch.content.parts:
                        parts.extend(batch.content.parts)
                beta_contents.append(types.Content(role="user", parts=parts))
        ...
        


                    
                

    except Exception:
        pass
