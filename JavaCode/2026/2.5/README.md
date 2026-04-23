# Java 反射学习 Demo

## 📚 什么是反射?

**反射就是在程序运行时,动态地操作类、方法、属性的能力。**

简单说:**在编译时不知道类名,运行时才根据字符串动态加载和操作类**。

---

## 🎯 为什么需要反射?

你现在可能觉得用不到,但反射是所有 Java 框架的基础:

- **Spring**: 根据配置文件自动创建对象 (IoC 容器)
- **MyBatis**: 根据 XML 配置动态调用方法
- **JUnit**: 自动发现和运行测试方法
- **JSON 库**: 把 JSON 字符串转成任意 Java 对象

**没有反射,就没有现代 Java 框架!**

---

## 📦 项目文件说明

### 1️⃣ `ReflectionDemo.java` - **综合演示 (推荐先看这个!)**

包含 5 个实用场景:

- ✅ **Demo 1**: 动态创建对象 (框架的基础)
- ✅ **Demo 2**: 调用私有方法 (测试/调试)
- ✅ **Demo 3**: 通用属性复制工具 (类似 Spring BeanUtils)
- ✅ **Demo 4**: 简易依赖注入框架 (模拟 Spring @Autowired)
- ✅ **Demo 5**: 查看类的完整信息 (API 文档生成器原理)

**运行方式:**
```bash
javac -encoding UTF-8 *.java
java ReflectionDemo
```

### 2️⃣ `ReflectionPlayground.java` - **交互式实验室**

自己动手体验反射!可以:

- 创建任意类的对象并设置属性
- 调用对象的方法
- 查看类的完整信息
- 暴力调用私有方法

**运行方式:**
```bash
java ReflectionPlayground
```

### 3️⃣ `ReflectionTest.java` - **基础示例**

你的原始代码,演示最基本的反射用法。

### 4️⃣ `User.java` - **示例类**

用于演示反射操作的用户类。

---

## 🚀 快速开始

### 1. 编译所有文件
```bash
cd A:\mycode\java\2026\2.5\src
javac -encoding UTF-8 *.java
```

### 2. 运行综合演示 (推荐!)
```bash
java ReflectionDemo
```

你会看到 5 个实用场景的演示,包括:
- 动态创建对象
- 调用私有方法
- 通用属性复制
- 依赖注入
- 类信息查看

### 3. 运行交互式实验室
```bash
java ReflectionPlayground
```

然后根据菜单选择你想体验的功能!

---

## 💡 核心知识点

### 1. 获取 Class 对象的三种方式

```java
// 方式1: 通过类名.class
Class<?> clazz1 = User.class;

// 方式2: 通过对象.getClass()
User user = new User();
Class<?> clazz2 = user.getClass();

// 方式3: 通过 Class.forName() (最常用,最灵活)
Class<?> clazz3 = Class.forName("User");
```

### 2. 反射创建对象

```java
// 无参构造
Object obj = clazz.getDeclaredConstructor().newInstance();

// 有参构造
Constructor<?> constructor = clazz.getConstructor(String.class, int.class);
Object obj = constructor.newInstance("张三", 25);
```

### 3. 反射访问属性

```java
Field field = clazz.getDeclaredField("name");
field.setAccessible(true);  // 突破访问控制
field.set(obj, "新值");     // 设置值
Object value = field.get(obj);  // 获取值
```

### 4. 反射调用方法

```java
Method method = clazz.getDeclaredMethod("sayHello");
method.setAccessible(true);  // 突破访问控制
method.invoke(obj);  // 调用方法
```

---

## 🔥 实际应用场景

### 场景 1: 通用对象复制工具

```java
// 不用反射: 要为每个类写复制方法
public void copyUser(User source, User target) { ... }
public void copyOrder(Order source, Order target) { ... }
// 太麻烦了!

// 用反射: 一个方法搞定所有类
public void copyProperties(Object source, Object target) {
    // 通用实现,见 ReflectionDemo.java
}
```

### 场景 2: 插件系统

```java
// 配置文件: plugin.properties
// plugin.class=com.example.MyPlugin

// 程序运行时动态加载
String pluginClass = config.get("plugin.class");
Class<?> clazz = Class.forName(pluginClass);
Plugin plugin = (Plugin) clazz.getDeclaredConstructor().newInstance();
plugin.execute();

// 用户明天写了新插件,你的代码一行不用改!
```

### 场景 3: 依赖注入框架 (Spring 原理)

```java
class UserService {
    @Inject  // 自定义注解
    private UserDao userDao;  // Spring 会自动注入
}

// 框架扫描 @Inject 注解,用反射自动注入
// 见 ReflectionDemo.java 的 Demo 4
```

---

## ⚠️ 注意事项

### 反射的缺点

1. **性能开销**: 反射比直接调用慢
2. **安全问题**: 可以访问私有成员
3. **代码难懂**: 编译时看不出调用关系

### 什么时候用反射?

✅ **应该用:**
- 写框架/工具类 (Spring, MyBatis)
- 写通用库 (JSON 序列化)
- 需要动态加载类 (插件系统)

❌ **不应该用:**
- 普通业务代码 (直接 new 对象就好)
- 追求性能的场景

---

## 🎓 学习建议

1. **先运行 ReflectionDemo.java** - 看 5 个实用场景
2. **再玩 ReflectionPlayground.java** - 自己动手体验
3. **看核心代码实现** - 理解原理
4. **思考实际应用** - 想想你的项目能用在哪里

**记住**: 现在感觉用不到很正常!等你用 Spring、写框架、做插件系统时,就会发现反射无处不在!

---

## 📖 扩展阅读

- Oracle 官方文档: [The Reflection API](https://docs.oracle.com/javase/tutorial/reflect/)
- Spring IoC 源码: 看看框架怎么用反射创建 Bean
- MyBatis 源码: 看看 ORM 框架怎么用反射

---

**祝你学习愉快! 有问题随时问 😊**
