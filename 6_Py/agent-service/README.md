# agent-service

这是一个用于学习 Python **包（package）**、**模块（module）** 和项目管理的最小示例。

## 目录说明

```text
agent-service/
├── pyproject.toml              # 项目元数据、构建方式和命令行入口
├── README.md                   # 项目说明
├── src/
│   └── agent_service/          # Python 包目录
│       ├── __init__.py         # 包初始化，并暴露公共 API
│       ├── main.py             # 可执行模块
│       ├── config.py            # 配置模块
│       └── tools/               # 子包
│           ├── __init__.py      # 子包初始化
│           └── calculator.py    # 计算器模块
└── tests/
    └── test_calculator.py      # 测试模块
```

## 包和模块

- 一个 `.py` 文件就是一个模块，例如 `config.py` 和 `calculator.py`。
- 包是一个包含 `__init__.py` 的目录，例如 `agent_service` 和 `agent_service.tools`。
- `agent_service.tools.calculator` 表示“包 `agent_service` 下的子包 `tools` 中的模块 `calculator`”。
- `__init__.py` 可以初始化包，也可以决定哪些名称作为公共 API 暴露。

示例导入：

```python
from agent_service import calculate
from agent_service.config import Settings
from agent_service.tools.calculator import add
```

## 安装项目

在本目录执行：

```bash
python -m pip install -e ".[dev]"
```

`-e` 表示 editable install（可编辑安装）。安装后，Python 会按照项目配置找到 `src/agent_service`，源码修改可以立即生效。

不安装也可以使用模块方式运行 CLI，但需要先把 `src` 加入 `PYTHONPATH`：

```bash
# Windows PowerShell
$env:PYTHONPATH = "src"
python -m agent_service.main 2 + 3
```

## 运行示例

安装后可以使用 `pyproject.toml` 中声明的命令：

```bash
agent-service 12 / 4
agent-service 2 ^ 8 --precision 3
```

也可以使用 Python 的模块运行方式：

```bash
python -m agent_service.main 12 / 4
```

支持的运算符：`+`、`-`、`*`、`/`、`^`。

## 配置

`config.py` 中的 `Settings` 从环境变量读取配置：

- `AGENT_SERVICE_NAME`：应用名称
- `AGENT_SERVICE_DEBUG`：是否开启调试，接受 `1`、`true`、`yes`、`on`
- `AGENT_SERVICE_PRECISION`：默认小数位数

命令行参数 `--precision` 优先于环境变量配置。

## 运行测试

```bash
python -m pytest
```

测试文件也是模块。它通过导入 `agent_service.tools.calculator`，验证包中的模块是否能被正确使用。
