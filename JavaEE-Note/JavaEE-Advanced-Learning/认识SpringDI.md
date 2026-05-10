


@[TOC](目录)
---

# 前言

这里给出代码，以免使得文章冗余
```java

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Student {

    private String name;
    private String gender;
    private String email;
    private Integer age;       
}
===========================================================================
@Service
public class Print {

    @Autowired
    private Student s1;
    
    public void print(){
        System.out.println(s1);
    }
}
===========================================================================
@Configuration
public class AppConfig {

    @Bean
    public Student student() {
        return new Student("Tom", "male",
                "tom@example.com", 18);
    }
}
==============================================================
public class SpringDiDemoApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(
                SpringDiDemoApplication.class, args);

        Print bean1 = context.getBean(Print.class);

        bean1.print();

    }
}
```

# 1. 属性注入

这是最简单的注入方式，将 `@Autowired` 加在需要注入的字段头上即可。
我们来看看效果。
![在这里插入图片描述](https://i-blog.csdnimg.cn/direct/18efe1353af14a4c8cd3afc08ec3bef1.png)

首先我们在配置类中通过 `@Bean` 注解，利用全参构造方法实例化了一个 Student 对象，并交由 Spring 容器管理（这里暂且称为 student bean）。
再利用 `@Autowired` 告诉 Spring，将这个 student bean 赋值给 `Print` 类中的 `s1` 字段。
最后在启动类中通过上下文获取到 `Print` 的 bean 实例并调用 `print` 方法。

---
# 2. 构造方法注入

当我们在 `Print` 类中实现了一个构造方法的时候，我们就可以通过这个构造方法来实现注入。
```java
 //构造方法注入

    private Student s2;
    public Print(Student s){
        this.s2 = s;
    }
```
这里的输出结果同上。

### 2.1 一些问题
构造方法注入确实是一个不错的方式，一个构造方法可以注入多个依赖。
假设我有多个构造方法呢？在 Spring 中会出现什么情况呢？？

我在 `Print` 类中添加了一个无参的构造函数
```java
private Student s2;
    public Print(Student s) {
        this.s2 = s;

    }

    public Print(){
    }
```
此时我运行 Spring 时，结果为
![在这里插入图片描述](https://i-blog.csdnimg.cn/direct/071b5e9c9bc74141ae89754d51275a29.png)
这里 `s2` 输出的数值为 null，也就说 `s2` 并未被赋值，也就意味着 Spring 选择了无参数的构造函数。
这个问题如何解决呢？
我们只需要将 `@Autowired` 这个注解添加到带参数的构造函数头上即可。
```java
 //构造方法注入
    private Student s2;
    @Autowired
    public Print(Student s) {
        this.s2 = s;
    }
    public Print(){
    }
```

运行结果
![在这里插入图片描述](https://i-blog.csdnimg.cn/direct/fc30aa4c98ef471dbd02d214af24719a.png)

这里讲解一下逻辑：

一开始，我们只有一个构造函数，Spring 无法选择只能用已有构造函数。
此时多了一个无参数的构造函数，由于无参的构造函数是默认的，所以 Spring 选择了无参数的构造函数。当我们在有参数的构造函数头上添加 `@Autowired` 注解，就等于让 Spring 明白我们希望使用有参数的构造函数来实现依赖注入。


---
# 3. setter注入

我们将通过 set 函数来实现注入
```java

    //setter注入
    private Student s2;
    @Autowired
    public void setS2(Student s2) {
        this.s2 = s2;
    }
```
此时的运行结果为：
![在这里插入图片描述](https://i-blog.csdnimg.cn/direct/e4733161073449bd984aeaadffc50658.png)



---

# 4. 三种注入方式对比

### 属性注入
优点是便捷，只需要写好字段添加注解就行了。
缺点是无法注入 `final` 修饰的字段。
要给被 `final` 修饰的字段赋值，只有两个方法，一种是声明的同时并赋值，一种是利用构造函数赋值。
问题是 `@Autowired` 属性注入是通过反射机制实现的。
首先，Spring 调用 `Print` 类的构造函数创建一个实例。
其次，在实例创建之后，Spring 才会去寻找标注了 `@Autowired` 的字段，并尝试通过反射把 Student 对象塞进去。
矛盾点：对于 `final` 字段，由于它在第一步实例化时没有被初始化，Java 根本不让你通过编译；

### 构造函数注入
优点是可以注入 `final` 修饰的字段。
![在这里插入图片描述](https://i-blog.csdnimg.cn/direct/e837b5b49c0b4fb1aa4fa19cbe7627b7.png)
通用性好，这是 JDK 支持的，其他框架下也可以使用这种方法来注入。
注入的对象无法改变。

### setter注入
优点是比较灵活。
由于是一个方法，也就可以重复调用，注入的对象可以发生改变。
缺点是无法注入 `final` 修饰字段。

---
# 5. @Autowired 存在的问题
目前我们从始至终都只在讨论一个类只创建一个对象，如何注入的问题。
在实际的开发环境中一个类创建多个对象是不可避免的。
现在我们用 `@Bean` 创建多个对象后，使用 `@Autowired` 会出现什么情况呢？？
```java

@Configuration
public class AppConfig {

    @Bean
    public Student student() {
        return new Student("Tom", "male",
                "tom@example.com", 18);
    }

    @Bean
    public Student student2() {
        return new Student("小明", "男",
                "小明@example.com", 28);
    }
}
========================================================================
	  @Autowired
    private Student s1;
    
    public void print(){
        System.out.println(s1);
    }
```

结果是报错了。
看看报错信息：`Could not autowire. There is more than one bean of 'Student' type`
因为容器中有多个 Student 类型的 Bean，这下 `@Autowired` 不知道该注入哪一个了。
如何解决呢？

1. 使用 @Primary
这个单词大家应该很熟悉，在数据库中有一个叫做主键的东西，它的英文就是 PrimaryKey。`@Primary` 的作用是标明哪一个对象优先注入。
![在这里插入图片描述](https://i-blog.csdnimg.cn/direct/39bcf74d58a94403b0976a3c54e02df3.png)
加上 `@Primary` 后，Spring 就会从多个对象中，选择由 `@Primary` 修饰的那一个拿出来注入。
不过这个注解我们用的比较少，我们更常用下面两个：

2. 使用 @Qualifier
这个注解就是通过添加名字来指定具体的 Bean：
![在这里插入图片描述](https://i-blog.csdnimg.cn/direct/aa7b37d843114851a654a4141862ddc6.png)
`@Qualifier` 中的字符串指定的是要注入的 Bean 的名称（在使用 `@Bean` 注解时，Bean 的默认名称就是方法名）。


3. 使用 @Resource
这个要更为简单一点，它是 Java 标准（JSR-250 规范）提供的注解，而非 Spring 原生。
它更像是 `@Qualifier` + `@Autowired` 的结合体。
![在这里插入图片描述](https://i-blog.csdnimg.cn/direct/b17db6838f654049ad0453cb0f74a251.png)



---
# 总结

1. 依赖注入（DI）的核心是将对象的创建权交给 Spring 容器，从而降低代码耦合度，提高可维护性。
2. 属性注入最简单但不支持 `final` 字段，底层是通过反射机制来实现的。
3. 构造方法注入是官方推荐方式，支持 `final` 字段；并且从 Spring 4.3 开始，若类只有一个构造函数，Spring 会自动注入，无需加 `@Autowired`。
4. setter 注入适合可选依赖，允许在运行期间通过方法调用更改注入的对象。
5. 当容器中有多个同类型的 Bean 时，`@Autowired`（默认按类型匹配）可以结合 `@Primary` 或 `@Qualifier` 来解决冲突。
6. `@Resource` 是 Java 标准注解，它默认按名称注入，如果按名称找不到再按类型匹配，同样可以解决多 Bean 冲突问题。
