import http.server
import socketserver
import threading
import subprocess
import time
import os

PORT = 8995
DIRECTORY = r"A:\Root_Code\Github_Workspace\6_Py\test-py\209"

class Handler(http.server.SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=DIRECTORY, **kwargs)
    def log_message(self, format, *args):
        pass

def run_server():
    with socketserver.TCPServer(("127.0.0.1", PORT), Handler) as httpd:
        httpd.serve_forever()

server_thread = threading.Thread(target=run_server, daemon=True)
server_thread.start()
time.sleep(1)

chrome_path = r"C:\Program Files\Google\Chrome\Application\chrome.exe"

views = ["panorama", "river", "temple", "hill"]
for v in views:
    out_file = os.path.join(DIRECTORY, f"screenshot_{v}.png")
    url = f"http://127.0.0.1:{PORT}/index.html?view={v}&test=1"
    cmd = [
        chrome_path,
        "--headless=new",
        "--no-sandbox",
        "--enable-webgl",
        "--ignore-gpu-blocklist",
        "--use-gl=angle",
        "--window-size=1600,900",
        f"--screenshot={out_file}",
        url
    ]
    t0 = time.time()
    res = subprocess.run(cmd, capture_output=True, timeout=30)
    print(f"Captured {v}: {os.path.exists(out_file)} in {time.time()-t0:.1f}s")
