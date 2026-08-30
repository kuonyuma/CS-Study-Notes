import time


class Sleep:
    """表示：当前协程希望等待多少秒"""

    def __init__(self, seconds: float):
        self.seconds = seconds


def task_a():
    print("A：开始")

    yield Sleep(2)

    print("A：等待 2 秒结束")

    yield Sleep(1)

    print("A：结束")


def task_b():
    print("B：开始")

    yield Sleep(1)

    print("B：等待 1 秒结束")

    yield Sleep(2)

    print("B：结束")


class Task:
    """包装一个协程，并保存它当前的运行状态"""

    def __init__(self, coroutine):
        self.coroutine = coroutine

        # 下一次允许执行的时间
        self.wake_time = 0

        # 是否已经执行结束
        self.done = False


class EventLoop:

    def __init__(self):
        self.tasks = []

    def create_task(self, coroutine):
        task = Task(coroutine)
        self.tasks.append(task)

    def run(self):

        while self.tasks:

            now = time.time()

            for task in self.tasks.copy():

                # 已完成
                if task.done:
                    self.tasks.remove(task)
                    continue

                # 还在等待
                if now < task.wake_time:
                    continue

                try:
                    # 恢复协程
                    event = next(task.coroutine)

                    # 协程告诉调度器：
                    # “我要 sleep”
                    if isinstance(event, Sleep):
                        task.wake_time = time.time() + event.seconds

                except StopIteration:
                    task.done = True

            # 防止 while 疯狂空转占满 CPU
            time.sleep(0.01)


loop = EventLoop()

loop.create_task(task_a())
loop.create_task(task_b())

loop.run()
