import time


class Sleep:
    def __init__(self, time) -> None:
        self.time = time

    def get_time(self):
        return self.time


def co_a():
    print("进入携程a")
    for i in range(1, 4):
        print(f"协程a执行任务{i}")
        yield Sleep(2)
    print("协程a任务执行完毕")


def co_b():
    print("进入携程b")
    for i in range(1, 4):
        print(f"协程b执行任务{i}")
        yield Sleep(2)
    print("协程b任务执行完毕")


# 书写调度器
class Task:
    def __init__(self, co) -> None:
        self.waketime = 0
        self.done = False
        self.coroutine = co


class Loop:
    def __init__(self) -> None:
        self.tasks: list[Task] = []

    def add_task(self, task: Task):
        self.tasks.append(task)

    def run(self):
        while self.tasks:
            now = time.time()
            for task in self.tasks.copy():
                if task.done:
                    self.tasks.remove(task)
                    continue
                if now < task.waketime:
                    continue

                try:
                    reuslt = next(task.coroutine)
                    task.waketime = now + reuslt.get_time()
                except StopIteration:
                    task.done = True


loop = Loop()
loop.add_task(Task(co_a()))
loop.add_task(Task(co_b()))

loop.run()
