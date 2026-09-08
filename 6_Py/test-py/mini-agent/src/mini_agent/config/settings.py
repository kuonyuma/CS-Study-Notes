from dataclasses import dataclass
import yaml
from pathlib import Path


@dataclass
class Model:
    name:str
    key:str

@dataclass
class AppConfig:
    model:Model
    system_prompt:str

BASE_DIR = Path(__file__).parent


def _load_config()->AppConfig:
    try:    
        with open(BASE_DIR / "config.yaml","r",encoding="utf-8") as f:
            config = yaml.safe_load(f)
    except (OSError,yaml.YAMLError) as exc:
        raise RuntimeError("配置文件加载出错") from exc

    if not isinstance(config,dict):
        raise TypeError("配置文件不是dict")
    if "model" not in config:
        raise ValueError("配置文件不完整")
    model = config["model"]
    if not isinstance(model,dict):
        raise TypeError("配置文件不是dict")

    keys = ("key","name")

    if any(key not in model for key in keys):
        raise ValueError("配置文件不完整")

    return AppConfig(
        model=Model(name=model["name"],key=model["key"]),
        system_prompt=(BASE_DIR / "system_prompt.md").read_text(encoding="utf-8") 
    )

setting = _load_config()
    
    