import subprocess
import  logging

def terminal(command:str)->str:
    """执行终端命令并返回输出。
    :param command: 要执行的终端命令。
    :return: 命令的标准输出和标准错误。
    """
    cmd = command
    if not cmd:
        return "命令为空"

    try:
        result = subprocess.run(
            cmd,
            shell=True,
            capture_output=True,
            timeout=30,
            cwd=None
        )
        try:
            stdout = result.stdout.decode("utf-8")
        except UnicodeError as e:
            logging.info(f"stdout用utf-8解码失败尝试用gbk。错误信息{e}")
            stdout = result.stdout.decode("gbk",errors="replace")
        try:
            stderr = result.stderr.decode("utf-8")
        except UnicodeError as e:
            logging.info(f"stderr用utf-8解码失败尝试用gbk。错误信息{e}")
            stderr  =result.stderr.decode("gbk",errors="replace")
        output = stdout
        if stderr:
            output += "\n[stderr]\n" + stderr
        return output if output else "(命令执行完毕，无输出)"
    except subprocess.TimeoutExpired as e:
        return f"错误:命令执行超时(30秒),{e}"
    except Exception as e:
        return f"错误：命令执行失败{e}"