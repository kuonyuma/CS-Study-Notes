import asyncio
import os
import sys

sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), "../..")))

from py_labs.lab04_agentic_loop.ui.app import main

if __name__ == "__main__":
    try:
        asyncio.run(main())
    except Exception as err:
        sys.stderr.write(f"Error: {err}\n")
        sys.exit(1)
