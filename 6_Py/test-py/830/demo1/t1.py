import yaml

with open("config.yaml", "r", encoding="utf-8") as f:
    config = yaml.safe_load(f)

# 反序列化


print(config)

data = {
    "port": 8080,
    "enabled": True
}

with open("config.yaml", "w", encoding="utf-8") as f:
    yaml.safe_dump(
        data,
        f,
        allow_unicode=True,
        sort_keys=False,
        default_flow_style=False,
    )
