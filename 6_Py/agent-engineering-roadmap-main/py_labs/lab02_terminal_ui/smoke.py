import asyncio
import os
import sys

sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), "../..")))

from py_labs.lab02_terminal_ui.app import App

async def smoke_test():
    app = App()
    print("Testing App turn execution...")
    await app.run_turn("Hello! Respond with one word: 'READY'.")

if __name__ == "__main__":
    try:
        asyncio.run(smoke_test())
        print("SMOKE OK")
    except Exception as err:
        sys.stderr.write(f"SMOKE FAIL: {err}\n")
        sys.exit(1)
