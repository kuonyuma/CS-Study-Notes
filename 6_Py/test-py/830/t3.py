import uuid

id_ = uuid.uuid4()
print(id_)

import subprocess

result = subprocess.run(["python", "--version"])

print(result)

result = subprocess.run(
    ["git", "status"],
    capture_output=True,
    text=True,
)

print(result.returncode)
print(result.stdout)
print(result.stderr)

