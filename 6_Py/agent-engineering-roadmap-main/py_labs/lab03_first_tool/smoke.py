import asyncio
import os
import sys

sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), "../..")))

from py_labs.lab03_first_tool.tools.list_files import ListFilesTool
from py_labs.lab03_first_tool.tools.read_file import ReadFileTool

async def smoke_test():
    list_tool = ListFilesTool()
    res1 = await list_tool.run({"path": "."})
    assert not res1.is_error, "list_files failed"
    assert "py_labs" in res1.content or "README.md" in res1.content

    read_tool = ReadFileTool()
    res2 = await read_tool.run({"path": "py_labs/requirements.txt"})
    assert not res2.is_error, "read_file failed"
    assert "google-genai" in res2.content

if __name__ == "__main__":
    try:
        asyncio.run(smoke_test())
        print("SMOKE OK")
    except Exception as err:
        sys.stderr.write(f"SMOKE FAIL: {err}\n")
        sys.exit(1)
