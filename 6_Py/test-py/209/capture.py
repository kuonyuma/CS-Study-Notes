import http.server
import socketserver
import threading
import subprocess
import time
import os

PORT = 8990
DIRECTORY = r"A:\Root_Code\Github_Workspace\6_Py\test-py\209"

class Handler(http.server.SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=DIRECTORY, **kwargs)
    def log_message(self, format, *args):
        pass # quiet

def run_server():
    with socketserver.TCPServer(("127.0.0.1", PORT), Handler) as httpd:
        httpd.serve_forever()

server_thread = threading.Thread(target=run_server, daemon=True)
server_thread.start()
time.sleep(1)

chrome_path = r"C:\Program Files\Google\Chrome\Application\chrome.exe"
screenshot_file = os.path.join(DIRECTORY, "screenshot_default.png")

cmd = [
    chrome_path,
    "--headless=new",
    "--no-sandbox",
    "--enable-webgl",
    "--ignore-gpu-blocklist",
    "--use-gl=angle",
    "--window-size=1600,900",
    f"--screenshot={screenshot_file}",
    f"http://127.0.0.1:{PORT}/index.html"
]

print("Running chrome screenshot...")
proc = subprocess.run(cmd, capture_output=True, text=True, timeout=20)
print("Returncode:", proc.returncode)
print("Stdout:", proc.stdout)
print("Stderr:", proc.stderr)
print("File exists:", os.path.exists(screenshot_file), "Size:", os.path.getsize(screenshot_file) if os.path.exists(screenshot_file) else 0)
