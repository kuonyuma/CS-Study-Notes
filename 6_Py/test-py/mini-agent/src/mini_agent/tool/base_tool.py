
# 工具的基类
from abc import ABC,abstractmethod
from dataclasses import dataclass
from typing import ClassVar,Any
from google.genai import types
@dataclass
class Result:
    content:str
    error:bool

class Tool(ABC):
    name: ClassVar[str]
    description: ClassVar[str]
    input_schema: ClassVar[dict[str, Any]]
    read_only: ClassVar[bool] = True

    @abstractmethod
    async def run(self, agument: dict[str, Any]) -> Result:
        pass

    def get_description(self)->types.FunctionDeclaration:
        return types.FunctionDeclaration(
            name=self.name,
            description=self.description,
            parameters=types.Schema(**self.input_schema)
        )