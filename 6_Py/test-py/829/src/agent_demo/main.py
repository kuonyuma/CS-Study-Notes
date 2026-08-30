import asyncio

from .agent.chat_service import chat_with_agent, create_conversation
from .db.engine import session_scope
from .db.init_db import init_db


async def main():
    await init_db()

    async with session_scope() as session:
        conversation = await create_conversation(session, title="Demo 对话")

    print(f"已创建会话 #{conversation.id}，输入 /exit 或 /quit 退出。")
    while True:
        parts = input("你：").strip().split(maxsplit=1)
        if len(parts) != 2:
            print("输出格式有错误")
            continue
        prompt = parts[1]
        conversation_id = int(parts[0])
        async with session_scope() as seesion:
            result = await chat_with_agent(
                session=seesion,
                query=prompt,
                conversation_id=conversation_id,
            )
            print(f"模型：{result}")


if __name__ == "__main__":
    print("开始测试")
    asyncio.run(main())
    print("测试结束")
