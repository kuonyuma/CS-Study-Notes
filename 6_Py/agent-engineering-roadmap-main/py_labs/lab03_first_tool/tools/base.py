from abc import ABC, abstractmethod
from dataclasses import dataclass
from typing import Dict, Any

@dataclass
class ToolResult:
    content: str
    is_error: bool = False

class Tool(ABC):
    name: str
    description: str
    input_schema: Dict[str, Any]
    read_only: bool = True

    @abstractmethod
    async def run(self, input_data: Dict[str, Any]) -> ToolResult:
        pass

    def to_gemini_declaration(self):
        from google.genai import types
        return types.FunctionDeclaration(
            name=self.name,
            description=self.description,
            parameters=self.input_schema,
        )
