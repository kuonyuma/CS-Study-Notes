import http.server
import socketserver
import threading
import subprocess
import time
import os

PORT = 8996
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

tasks = [
    ("screenshot_panorama.png", "1600,900", "http://127.0.0.1:8996/index.html?view=panorama&atmo=bluehour&test=1"),
    ("screenshot_river.png", "1600,900", "http://127.0.0.1:8996/index.html?view=river&atmo=bluehour&test=1"),
    ("screenshot_temple.png", "1600,900", "http://127.0.0.1:8996/index.html?view=temple&atmo=bluehour&test=1"),
    ("screenshot_hill.png", "1600,900", "http://127.0.0.1:8996/index.html?view=hill&atmo=bluehour&test=1"),
    ("screenshot_morning.png", "1600,900", "http://127.0.0.1:8996/index.html?view=river&atmo=morning&test=1"),
    ("screenshot_rain.png", "1600,900", "http://127.0.0.1:8996/index.html?view=river&atmo=rain&test=1"),
    ("screenshot_mobile.png", "412,915", "http://127.0.0.1:8996/index.html?view=river&atmo=bluehour&test=1")
]

for filename, size, url in tasks:
    out_file = os.path.join(DIRECTORY, filename)
    cmd = [
        chrome_path,
        "--headless=new",
        "--no-sandbox",
        "--enable-webgl",
        "--ignore-gpu-blocklist",
        "--use-gl=angle",
        f"--window-size={size}",
        f"--screenshot={out_file}",
        url
    ]
    t0 = time.time()
    res = subprocess.run(cmd, capture_output=True, timeout=30)
    print(f"{filename} ({size}): {os.path.exists(out_file)} ({os.path.getsize(out_file) if os.path.exists(out_file) else 0} bytes, {time.time()-t0:.1f}s)")
