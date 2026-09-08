class Dog:
    def bark(self, message):
        print(f"self 是: {self}, 叫声是: {message}")

dog = Dog()

# 1. 观察 Dog.bark 与 dog.bark 的不同
print(Dog.bark)
# 输出: <function Dog.bark at 0x...> （这是纯函数）

print(dog.bark)
# 输出: <bound method Dog.bark of <__main__.Dog object at 0x...>> 
# （看到 bound method 了吗？它就是绑定了 dog 实例的方法对象）

# 2. 检查包装盒里的 __self__ 和 __func__（对应截图 2）
print(dog.bark.__self__ is dog)       # 输出: True （里面记住了 dog）
print(dog.bark.__func__ is Dog.bark) # 输出: True （里面记住了纯函数 Dog.bark）

# 3. 验证调用方式的完全等价性：
dog.bark("wang")        # ① 日常写法：包装盒自动帮我们把 dog 作为 self 传进去
Dog.bark(dog, "wang")   # ② 底层等价写法：手动把 dog 传给 self

# 两种写法的运行结果一模一样！