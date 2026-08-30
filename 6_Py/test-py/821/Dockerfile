FROM python:3.14-slim

WORKDIR /app

COPY --from=ghcr.io/astral-sh/uv:latest /uv /uvx /usr/local/bin/

COPY pyproject.toml uv.lock ./

COPY src ./src

RUN uv sync --frozen

CMD [ "uv","run","-m","demo_project.main" ]
