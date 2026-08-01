"""流式输出逻辑测试。

不依赖真实 Gemini API：用假 SDK 对象验证事件流、跨 chunk 函数调用合并、
Agent 工具循环、重试与上下文压缩。

运行：python test/test_streaming.py
"""

import asyncio
import sys
import tempfile
import types as _pytypes
from pathlib import Path
from unittest import mock

BASE_DIR = Path(__file__).resolve().parent.parent
if str(BASE_DIR) not in sys.path:
    sys.path.append(str(BASE_DIR))


# ---------- 假 google.genai ----------


class FakeConfig:
    def __init__(self, **kwargs):
        for key, value in kwargs.items():
            setattr(self, key, value)


class FakeFunctionCall:
    def __init__(self, name="", args=None):
        self.name = name
        self.args = args or {}


class FakePart:
    def __init__(self, text="", function_call=None, function_response=None, **kwargs):
        self.text = text
        self.function_call = function_call
        self.function_response = function_response
        for key, value in kwargs.items():
            setattr(self, key, value)

    @classmethod
    def from_text(cls, text=""):
        return cls(text=text)

    @classmethod
    def from_function_response(cls, name="", response=None):
        return cls(function_response={"name": name, "response": response})


class FakeContent:
    def __init__(self, role="", parts=None, **kwargs):
        self.role = role
        self.parts = parts or []
        for key, value in kwargs.items():
            setattr(self, key, value)


class FakeTool:
    def __init__(self, function_declarations=None):
        self.function_declarations = function_declarations or []


class FakeFunctionDeclaration:
    @classmethod
    def from_callable_with_api_option(cls, callable=None, **kwargs):
        return {"name": callable.__name__}


_fake_types = _pytypes.ModuleType("google.genai.types")
for _name, _cls in {
    "GenerateContentConfig": FakeConfig,
    "FunctionCall": FakeFunctionCall,
    "Part": FakePart,
    "Content": FakeContent,
    "Tool": FakeTool,
    "FunctionDeclaration": FakeFunctionDeclaration,
}.items():
    setattr(_fake_types, _name, _cls)

_fake_genai = _pytypes.ModuleType("google.genai")
_fake_genai.types = _fake_types
_fake_genai.Client = object
_fake_google = _pytypes.ModuleType("google")
_fake_google.genai = _fake_genai
sys.modules.setdefault("google", _fake_google)
sys.modules["google.genai"] = _fake_genai
sys.modules["google.genai.types"] = _fake_types


# ---------- 假流式 chunk / client ----------


class FakeUsage:
    def __init__(self, prompt=0, candidates=0):
        self.prompt_token_count = prompt
        self.candidates_token_count = candidates


class FakeCandidate:
    def __init__(self, parts=None, finish_reason=None):
        self.content = FakeContent(role="model", parts=parts or [])
        self.finish_reason = finish_reason


class FakeChunk:
    def __init__(self, parts=None, finish_reason=None, usage=None):
        self.candidates = [FakeCandidate(parts=parts or [], finish_reason=finish_reason)]
        self.usage_metadata = usage


class FakeModels:
    def __init__(self, chunks):
        self.chunks = chunks
        self.last_config = None

    async def generate_content_stream(self, *, model, contents, config=None):
        self.last_config = config

        async def _iter():
            for chunk in self.chunks:
                yield chunk

        return _iter()


class FakeAio:
    def __init__(self, chunks):
        self.models = FakeModels(chunks)


class FakeClient:
    def __init__(self, chunks):
        self.aio = FakeAio(chunks)


# ---------- 测试 ----------


def _collect(agen):
    events = []

    async def _run():
        async for event in agen:
            events.append(event)

    asyncio.run(_run())
    return events


def test_generate_stream_merges_function_calls():
    import llm.gemini as gemini_mod

    chunks = [
        FakeChunk(
            parts=[
                FakePart(text="你好"),
                FakePart(function_call=FakeFunctionCall(name="read", args={})),
            ]
        ),
        FakeChunk(
            parts=[
                FakePart(function_call=FakeFunctionCall(name="read", args={"path": "a.txt"}))
            ]
        ),
        FakeChunk(
            parts=[
                FakePart(
                    function_call=FakeFunctionCall(
                        name="write", args={"path": "b.txt", "content": "x"}
                    )
                )
            ]
        ),
        FakeChunk(finish_reason="STOP", usage=FakeUsage(10, 20)),
    ]
    client = FakeClient(chunks)

    events = _collect(
        gemini_mod.generate(
            client, "gemini-test", contents="hi", system_instruction="sys"
        )
    )

    assert [e.type for e in events] == ["text", "tool_use", "tool_use", "end"]
    assert events[0].text == "你好"

    end = events[-1].result
    assert end.full_text == "你好", "full_text 不应有前导空格"
    assert end.used == {"input_tokens": 10, "output_tokens": 20}
    assert end.stop_reason == "tool_use"
    assert [c.name for c in end.function_calls] == ["read", "write"]
    assert end.function_calls[0].args == {"path": "a.txt"}

    # 历史中的函数调用必须是合并后的完整 part
    fc_parts = [p for p in end.model_content.parts if p.function_call]
    assert len(fc_parts) == 2
    assert fc_parts[0].function_call.args == {"path": "a.txt"}

    # config 中应包含 tools 配置
    assert client.aio.models.last_config.tools is None


def test_generate_merges_args_only_continuation():
    import llm.gemini as gemini_mod

    chunks = [
        FakeChunk(parts=[FakePart(function_call=FakeFunctionCall(name="read", args={}))]),
        FakeChunk(parts=[FakePart(function_call=FakeFunctionCall(args={"path": "x.txt"}))]),
        FakeChunk(finish_reason="STOP"),
    ]
    end = _collect(gemini_mod.generate(FakeClient(chunks), "m", contents="q"))[-1].result
    assert len(end.function_calls) == 1
    assert end.function_calls[0].name == "read"
    assert end.function_calls[0].args == {"path": "x.txt"}


def test_generate_yields_error_event():
    import llm.gemini as gemini_mod

    class BrokenModels:
        async def generate_content_stream(self, **kwargs):
            async def _iter():
                raise RuntimeError("boom")
                yield  # pragma: no cover

            return _iter()

    class BrokenClient:
        class Aio:
            models = BrokenModels()

        aio = Aio()

    events = _collect(gemini_mod.generate(BrokenClient(), "m", contents="q"))
    assert [e.type for e in events] == ["error"]
    assert events[0].text == "boom"


def test_agent_streams_tool_execution_and_end():
    import llm.gemini as gemini_mod
    import swe.agent as agent_mod
    from Tools.read import read as read_file

    with tempfile.TemporaryDirectory() as tmpdir:
        target = Path(tmpdir) / "demo.txt"
        target.write_text("hello", encoding="utf-8")

        generate_calls = []

        async def fake_generate(client, model_name, contents=None, system_instruction=None, function_calls=None):
            generate_calls.append((len(contents), function_calls))
            if len(generate_calls) == 1:
                fc = gemini_mod.types.FunctionCall(name="read", args={"path": str(target)})
                result = gemini_mod.stream_result(
                    used={},
                    stop_reason="tool_use",
                    full_text="",
                    function_calls=[fc],
                    model_content=gemini_mod.types.Content(
                        role="model", parts=[gemini_mod.types.Part(function_call=fc)]
                    ),
                )
                yield gemini_mod.stream_event(type="tool_use", name="read")
                yield gemini_mod.stream_event(type="end", result=result)
            else:
                result = gemini_mod.stream_result(
                    used={"input_tokens": 1, "output_tokens": 1},
                    stop_reason="STOP",
                    full_text="完成",
                    function_calls=[],
                    model_content=gemini_mod.types.Content(
                        role="model", parts=[gemini_mod.types.Part(text="完成")]
                    ),
                )
                yield gemini_mod.stream_event(type="end", result=result)

        with mock.patch.object(gemini_mod, "generate", new=fake_generate):
            agent = agent_mod.Agent(client=object(), model_name="gemini-test")
            agent.register(read_file)
            events = _collect(agent.execute("读取文件"))

        assert [e.type for e in events] == ["start", "tool_use", "end"]
        assert events[1].name == "read"
        assert events[-1].result.full_text == "完成"
        # 注册了工具时，必须把 function_calls 传给模型
        assert generate_calls[0][1] is not None
        assert generate_calls[0][1][0].function_declarations[0]["name"] == "read"
        # 第二次调用时历史应为: 用户请求 + 模型调用 + 工具响应
        assert generate_calls[1][0] == 3


def test_agent_retries_then_reports_error():
    import llm.gemini as gemini_mod
    import swe.agent as agent_mod

    async def fake_generate(client, model_name, contents=None, system_instruction=None, function_calls=None):
        yield gemini_mod.stream_event(type="error", text="API down")

    with mock.patch.object(gemini_mod, "generate", new=fake_generate):
        agent = agent_mod.Agent(client=object(), model_name="gemini-test")
        agent.max_retry_budget = 2
        events = _collect(agent.execute("重试测试"))

    assert [e.type for e in events] == ["start", "error"]
    assert events[-1].text == "API down"
    assert agent.consecutive_errors == 2


def test_compress_contents_fallback_and_summary():
    import llm.gemini as gemini_mod
    import memory.memory_manager as memory_mod

    contents = [
        gemini_mod.types.Content(role="user", parts=[gemini_mod.types.Part.from_text(f"q{i}")])
        for i in range(5)
    ]

    async def fake_generate(client, model_name, contents=None, system_instruction=None, function_calls=None):
        result = gemini_mod.stream_result(
            used={}, stop_reason="STOP", full_text="历史摘要", function_calls=[]
        )
        yield gemini_mod.stream_event(type="end", result=result)

    with mock.patch.object(gemini_mod, "generate", new=fake_generate):
        compressed = asyncio.run(memory_mod.compress_contents(contents, object(), "m"))

    assert len(compressed) == 3
    assert "历史摘要" in compressed[0].parts[-1].text

    # 摘要失败时应原样返回历史
    async def failing_generate(client, model_name, contents=None, system_instruction=None, function_calls=None):
        yield gemini_mod.stream_event(type="error", text="失败")

    with mock.patch.object(gemini_mod, "generate", new=failing_generate):
        unchanged = asyncio.run(memory_mod.compress_contents(contents, object(), "m"))
    assert unchanged == contents


if __name__ == "__main__":
    tests = [
        test_generate_stream_merges_function_calls,
        test_generate_merges_args_only_continuation,
        test_generate_yields_error_event,
        test_agent_streams_tool_execution_and_end,
        test_agent_retries_then_reports_error,
        test_compress_contents_fallback_and_summary,
    ]
    failed = 0
    for test in tests:
        try:
            test()
            print(f"PASS {test.__name__}")
        except Exception:
            failed += 1
            import traceback

            traceback.print_exc()
            print(f"FAIL {test.__name__}")
    print(f"\n{len(tests) - failed}/{len(tests)} passed")
    sys.exit(1 if failed else 0)
