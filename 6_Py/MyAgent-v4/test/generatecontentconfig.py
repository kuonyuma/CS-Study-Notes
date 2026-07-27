from google.genai import types


config = types.GenerateContentCongig(
    tools=[read, write],
    temperature=0.0,
    system_instruction="你是agent",
    max_output_tokens=8192,
)

self.contents.append(
    types.Content(role="user", parts=[types.Part.from_text(text="请帮我...")])
)

output = self.tools["read"](path="test/add.py")

self.contents.append(
    types.Content(
        role="user",
        parts=[
            types.Part.from_function_response(
                name="read", response=["result", str(output)]
            )
        ],
    )
)

response = client.models.generate_content()

if response.text:
    return f"llm返回:{response.text}"

if response.function_calls:
    for call in response.function_calls:
        tool_name = call.name
        args = call.args
        result = self.tools[tool_name](**args)
        result = result.candidates[0].content

self.contents.append(
    types.Content(
        role="mll",
        parts=type.Part.from_function_response(
            name=tool_name, response=["result", result]
        ),
    )
)
