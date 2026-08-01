import subprocess
import logging


def terminal(command: str) -> str:
    """执行终端命令并返回输出。

    :param command: 要执行的终端命令。
    :return: 命令的标准输出（成功时）。
    :raises ValueError: 命令为空时抛出。
    :raises subprocess.TimeoutExpired: 命令执行超时（30秒）时抛出。
    :raises RuntimeError: 命令退出码非 0 时抛出。
    """
    if not command:
        raise ValueError("命令为空")

    result = subprocess.run(
        command,
        shell=True,
        capture_output=True,
        timeout=30,
        cwd=None,
    )

    try:
        stdout = result.stdout.decode("utf-8")
    except UnicodeError as e:
        logging.info(f"stdout用utf-8解码失败尝试用gbk。错误信息{e}")
        stdout = result.stdout.decode("gbk", errors="replace")

    try:
        stderr = result.stderr.decode("utf-8")
    except UnicodeError as e:
        logging.info(f"stderr用utf-8解码失败尝试用gbk。错误信息{e}")
        stderr = result.stderr.decode("gbk", errors="replace")

    output = stdout
    if stderr:
        output += "\n[stderr]\n" + stderr

    if result.returncode != 0:
        raise RuntimeError(f"命令执行失败，退出码 {result.returncode}:\n{output}")

    return output if output else "(命令执行完毕，无输出)"
