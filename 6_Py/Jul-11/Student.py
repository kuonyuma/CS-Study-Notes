
class Student:

    ClassName = "高三八班"

    def __init__(self,name,age):
        self.name = name
        self.age = age

    def __str__(self):
        return f"{self.name} and {self.age}"

    def run(self):
        print(f"你好，我是学生 {self.name}")

    def eat(self):
        print(f"{self.name}:正在吃饭")