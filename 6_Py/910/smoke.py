"""使用真实 MySQL/Redis 验证；先启动 docker compose。仅删除本次创建的图书。"""

from fastapi.testclient import TestClient

from main import app

# 上下文进入/退出会执行应用 lifespan，无需另外启动 uvicorn。
with TestClient(app) as client:
    result = client.post("/books", json={"title": "Python 入门", "author": "示例作者"})
    assert result.status_code == 201, result.text
    book_id = result.json()["id"]
    path = f"/books/{book_id}"
    try:
        result = client.get(path)
        assert result.status_code == 200, result.text
        assert result.headers["X-Cache"] == "MISS"
        result = client.get(path)
        assert result.headers["X-Cache"] == "HIT"
        result = client.get("/books")
        assert result.status_code == 200, result.text
        assert any(book["id"] == book_id for book in result.json())
        result = client.put(path, json={"title": "Python 进阶", "author": "示例作者"})
        assert result.status_code == 200, result.text
        result = client.get(path)
        assert result.json()["title"] == "Python 进阶"
        assert result.headers["X-Cache"] == "MISS"
        assert client.post("/books", json={"title": "", "author": "作者"}).status_code == 422
    finally:
        result = client.delete(path)
        assert result.status_code == 204, result.text
    assert client.get(path).status_code == 404
    assert client.put(path, json={"title": "书", "author": "作者"}).status_code == 404
    assert client.delete(path).status_code == 404

print("PASS: CRUD、缓存命中与失效、输入验证、404、应用启动与关闭")
