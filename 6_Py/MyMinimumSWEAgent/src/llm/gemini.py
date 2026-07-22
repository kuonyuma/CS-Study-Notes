"""
Gemini LLM 封装模块

作用：封装对 Google Gemini API 的调用，让 Agent 只需调用 generate() 就能拿到回复。

核心概念：
- client: Google genai 的客户端，负责网络通信
- model_name: 使用哪个模型（如 gemini-2.5-flash）
- temperature=0.0: 让输出尽可能确定、不随机（Agent 需要稳定的 JSON 输出）
"""
import sys
from pathlib import Path

project_root = str(Path(__file__).resolve().parent.parent.parent)
if project_root not in sys.path:
    sys.path.insert(0, project_root)

from google import genai
from google.genai import types
from src.config.logging import logger
from typing import Optional


def generate(client: genai.Client, model_name: str, prompt: str) -> Optional[str]:
    """
    调用 Gemini 生成回复

    Args:
        client: genai 客户端实例
        model_name: 模型名称
        prompt: 发送给模型的完整 prompt

    Returns:
        模型的文本回复，失败则返回 None
    """
    try:
        logger.info("正在调用 Gemini 生成回复...")

        response = client.models.generate_content(
            model=model_name,
            contents=prompt,
            config=types.GenerateContentConfig(
                temperature=0.0,     # 不随机，保证输出稳定
                max_output_tokens=8192,
            )
        )

        if not response.text:
            logger.error("Gemini 返回了空响应")
            return None

        logger.info("成功获取 Gemini 回复")
        return response.text

    except Exception as e:
        logger.error(f"调用 Gemini 出错: {e}")
        return None
