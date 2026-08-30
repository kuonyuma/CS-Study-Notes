from typing import Literal

from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from .ask_llm import ask_gemini
from ..db.models import Conversation, Message

MessageRole = Literal["user", "model"]


class ConversationNotFoundError(LookupError):
    """Raised when an agent request references a missing conversation."""


class EmptyModelResponseError(RuntimeError):
    """Raised when the model does not return any text."""


async def create_conversation(session: AsyncSession, title: str) -> Conversation:
    conversation = Conversation()
    conversation.title = title
    session.add(conversation)
    await session.commit()
    return conversation


async def get_conversation(
    session: AsyncSession, conversation_id: int
) -> Conversation | None:
    return await session.get(Conversation, conversation_id)


async def add_message(
    session: AsyncSession,
    conversation_id: int,
    role: MessageRole,
    content: str,
) -> Message:
    message = Message(
        conversation_id=conversation_id,
        role=role,
        content=content,
    )
    session.add(message)
    await session.commit()
    return message


async def load_history(
    session: AsyncSession, conversation_id: int
) -> list[dict[str, str]]:
    result = await session.execute(
        select(Message)
        .where(Message.conversation_id == conversation_id)
        .order_by(Message.id)
    )

    messages = result.scalars().all()
    return [{"role": message.role, "content": message.content} for message in messages]


async def chat_with_agent(
    session: AsyncSession,
    conversation_id: int,
    query: str,
) -> str:
    """Persist a user query, ask the agent, and persist the model response."""
    if not query.strip():
        raise ValueError("Query must not be empty")

    conversation = await get_conversation(session, conversation_id)
    if conversation is None:
        raise ConversationNotFoundError(
            f"Conversation {conversation_id} does not exist"
        )

    await add_message(
        session=session,
        conversation_id=conversation_id,
        role="user",
        content=query,
    )

    history = await load_history(session=session, conversation_id=conversation_id)
    chunks: list[str] = []
    async for chunk in ask_gemini(history):
        chunks.append(chunk)

    answer = "".join(chunks)
    if not answer:
        raise EmptyModelResponseError("Model returned no text")

    await add_message(
        session=session,
        conversation_id=conversation_id,
        role="model",
        content=answer,
    )
    return answer
