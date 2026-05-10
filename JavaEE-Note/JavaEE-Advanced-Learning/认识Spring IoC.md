




@[TOC](目录)

---

# 前言

通过本篇文章你将了解到Spring IoC的思想、五大类注解、方法注解,DI。

---

# 1. 了解Spring

Spring被称为一个框架，有着庞大的社区，支持很多应用场景。
简单来说就是一个包含了众多对象的容器。
不过这里的容器有一个前缀，叫做IoC。
也就是IoC容器。

容器这个词没那么高深，我们日常生活中经常能看见。
容器嘛就是装东西的。
比如一瓶矿泉水，里面的水是溶剂，空瓶子就是容器。
化学实验室里的烧杯也是一个容器。
我们说Spring是一个容器，只不过是把这个词语稍微抽象了一点。以前我们需要自己去new一个对象，现在Spring将一个个的对象（Object）当作溶剂。Spring把它们装在一起，帮我们统一管理。
再比如，我们使用的HashMap用来存储数据，它也是一个容器。


---
# 2. IoC
Spring IoC是Spring中一个比较重要的思想，其中另一个重要的思想是AOP。

这里我们先了解IoC是什么。
首先IoC全称叫做Inversion of Control。翻译过来就是控制反转。
这里的控制可以理解为，对象的控制权。
反转也就是白的变成黑的，主动变成被动。
所以控制反转就是掌握对象创建与销毁的人改变了。

再举出一个例子，一个小公司。
产品研发，人员招聘...这些事情都可以由老板一个人做。
等到公司规模扩大，老板就会把招聘这个事情分出去，让HR来做。
把产品研发交给技术团队去做。
看，掌管人员招聘这个事情的人就变了，由最初的老板控制变为现在的HR控制。

所以Spring IoC就是将对象的控制权转移到Spring上，由Spring来决定对象的创建与销毁。

我们再通过代码来体会这个反转思想。
```java
public class NewCarExample {
    public static void main(String[] args) {
        Car car = new Car();
        car.run();
    }

    /**
     * 汽车对象
     */
    static class Car {
        private Framework framework;

        public Car() {
            // 直接在构造函数中 new 下层依赖，导致高耦合
            framework = new Framework();
            System.out.println("Car init....");
        }

        public void run() {
            System.out.println("Car run...");
        }
    }

    /**
     * 车身类
     */
    static class Framework {
        private Bottom bottom;

        public Framework() {
            bottom = new Bottom();
            System.out.println("Framework init...");
        }
    }

    /**
     * 底盘类
     */
    static class Bottom {
        private Tire tire;

        public Bottom() {
            tire = new Tire();
            System.out.println("Bottom init...");
        }
    }

    /**
     * 轮胎类
     */
    static class Tire {
        public Tire() {
            System.out.println("Tire init...");
        }
    }
}
```

造车之前我们得将车身造出来，建造车身又得建造底盘....
这样就形成了这样的一个关系：车->车身->底盘->轮胎。
这种写法的耦合度是非常高的。

我们从中选择一个类来具体研究
```java
   static class Bottom {
        private Tire tire;

        public Bottom() {
            tire = new Tire();
            System.out.println("Bottom init...");
        }
    }
```

这个Bottom的建造依赖于Tire这个对象，我们就说Tire是Bottom的一个依赖。
这个Tire是在Bottom的构造方法里面创建出来的，也就是说，Tire这个对象的存活，什么时候创建、销毁，完全由Bottom这个对象决定。

我们再来看另一种写法。
```java

public class NewCarExample {
    public static void main(String[] args) {
        // --- Main 方法作为容器，负责“控制”对象的生命周期和依赖关系 ---
        
        // 1. 先生产最底层的零件
        Tire tire = new Tire(18); // 假设现在轮胎可以指定尺寸了
        
        // 2. 将零件注入到上一层组件中 (构造方法注入)
        Bottom bottom = new Bottom(tire);
        
        // 3. 继续向上注入
        Framework framework = new Framework(bottom);
        
        // 4. 最后组装成汽车
        Car car = new Car(framework);
        
        // 5. 运行
        car.run();
    }

    /**
     * 汽车对象
     */
    static class Car {
        private Framework framework;

        // IoC：通过构造函数注入依赖，不再自己 new
        public Car(Framework framework) {
            this.framework = framework;
            System.out.println("Car init....");
        }

        public void run() {
            System.out.println("Car run...");
        }
    }

    /**
     * 车身类
     */
    static class Framework {
        private Bottom bottom;

        public Framework(Bottom bottom) {
            this.bottom = bottom;
            System.out.println("Framework init...");
        }
    }

    /**
     * 底盘类
     */
    static class Bottom {
        private Tire tire;

        public Bottom(Tire tire) {
            this.tire = tire;
            System.out.println("Bottom init...");
        }
    }

    /**
     * 轮胎类
     */
    static class Tire {
        private int size;
        public Tire(int size) {
            this.size = size;
            System.out.println("Tire init, size: " + size);
        }
    }
}
```

这个写法就是利用了IoC思想，对象的控制权属于main这个方法，
所有对象都在main里面创建。


---
# 3. 注解

好了，IoC思想我们已经体会到了，接下来看看如何在Spring中用上。

这是一个类注解，当你将这个注解添加到类上方时，Spring就会管理这个类，并创建一个对象出来。
```java
@Controller // 将对象存储到 Spring 中 
public class UserController {
 	public void sayHi(){
  		System.out.println("hi,UserController...");
  		}
}
```
当Spring运行的时候，这个UserController对象就已经被创建出来了。

既然对象已经被创建出来了，那我们该怎么验证（或者获取）它呢？毕竟眼见为实对吧。
```java
@SpringBootApplication
public class IoCDemoApplication {
    //测试Bean
    public static void main(String[] args){
        ApplicationContext context = SpringApplication.run(
                IoCDemoApplication.class,args
        );
		UserController bean1 = context.getBean(UserController.class);
        bean1.sayHi();
        //控制台输出hi,UserController...
    }
```

其中这一段代码你就理解为获取Spring中的应用程序上下文即可。
这里的上下文也可以理解为拿到Spring这个容器。
```java
  ApplicationContext context = SpringApplication.run(
                IoCDemoApplication.class,args
        );
```

这一段代码就是从上下文（容器）中获取一个类型为UserController的对象。
```java
UserController bean1 = context.getBean(UserController.class);
```

除了Controller这一个类注解，还有其他四个Configuration, Component, Service, Repository。这四个类注解的使用方式几乎可以说是一样的，这里就不演示了。

但是我们要来看看这些注解之间的区别。
在三层架构中，@Controller 代表控制层，负责接收前端的请求；
@Service 是业务层，负责对业务逻辑和数据进行处理；
@Repository 是数据层，主要面向数据库层面的操作；
@Configuration 是配置层，先按下不表。

为什么有这么多功能相似的注解呢？这就和每个省市都有自己的车牌号前缀是一样的道理。

车牌号都是唯一的，用来标识一辆车。但为什么还要设置不同的车牌开头呢？
比如陕西的车牌号是【陕A/B/C...】，北京的车牌号是【京A/B/C...】。通过车牌开头，我们一眼就能知道这辆车是哪个地区的。
注解也是一样，它们都能把对象存进Spring，但不同的注解能让我们一眼看清这个类是属于哪一层（控制层/业务层/数据层）的。

比较重要的区别是Controller不能够和其他注解混用，不然当你输入URL时浏览器页面会报404。
```java
@RequestMapping("/UserController")
@Controller
//@Configuration
@ResponseBody
public class UserController {

    @RequestMapping("/print")
    public String print() {
       return "使用 UserController";
    }
}
```
正常结果
![在这里插入图片描述](https://i-blog.csdnimg.cn/direct/31877d641b8a4a6298c0fb05fc822b65.png)

错误代码
```java
@RequestMapping("/UserController")
@Controller
@Configuration
public class UserController {

    @RequestMapping("/print")
    public String print() {
       return "使用 UserController";
    }
}
```
错误结果
![在这里插入图片描述](https://i-blog.csdnimg.cn/direct/feb395f18ee4403591bb5ca7f4c3dcd2.png)


---

# 4. Bean

在前面的内容里，我们一直用“对象”这个词。但在 Spring 的世界里，被 Spring 容器管理的对象有一个专属的高级名字——**Bean（Java Bean）**。

你可以这么理解：如果在外面，它就是一个普普通通的 Java 对象（Object）；一旦它被贴上 `@Controller` 或 `@Service` 等注解，被招进了 Spring 容器，它就摇身一变成为了 **Spring Bean**。就像一个普通人，入职了公司，就成了公司的“打工人”一样。

我们前面讲的 `@Controller` 等五大类注解，是贴在**类**上面的。但如果你不想把整个类都交给 Spring，只想把类里面的某个**方法的返回值**交给 Spring 管理呢？

这时候就要用到我们前言里提到的**方法注解：`@Bean`** 了。

```java
@Configuration // 通常 @Bean 要配合 @Configuration 一起使用
public class AppConfig {

    // 使用 @Bean 注解，告诉 Spring：把这个方法的返回值当成一个 Bean 存起来！
    @Bean
    public User user() {
        User user = new User();
        user.setName("张三");
        user.setAge(18);
        return user;
    }
}
```
这样，一个名字叫张三的 User 对象（Bean）就被存进 Spring 容器里了，随时待命！

---

# 5. 了解DI

现在，我们的 Spring 容器里已经装满了各种各样的 Bean（对象）了。但程序要跑起来，对象和对象之间是需要互相配合的。

比如，控制层的 `UserController` 想要调用业务层的 `UserService`。
如果是以前，我们要在 `UserController` 里面自己去 `new UserService()`。
有了 Spring 之后呢？既然它们都在容器里，难道每次都要写一串 `context.getBean(UserService.class)` 吗？那也太麻烦了。

这时候，就轮到 **DI（Dependency Injection，依赖注入）** 了。

很多人容易把 IoC 和 DI 搞混。其实很简单：
*   **IoC 是一种思想**（把控制权交出去）。
*   **DI 是这种思想的具体实现手段**（怎么把需要的对象拿过来用）。

Spring 提供了一个超级好用的注解：**`@Autowired`**。只要贴上它，Spring 就会自动把你要的对象给你“注入”进来。

我们来看看代码有多简洁：

```java
@Controller
public class UserController {
    
    // 划重点：贴上 @Autowired，Spring 就会自动从容器里找 UserService 塞给你！
    @Autowired
    private UserService userService;

    public void sayHi() {
        System.out.println("hi, UserController...");
        // 直接用 userService，完全不需要 new！
        // userService.doService();
    }
}
```


---
# 总结

1. **理解 IoC 思想**：创建和销毁对象的“控制权”交给了 Spring 容器，让 Spring 来帮我们统一管理，这就是控制反转
4. **存入 Bean 对象**：通过注解我们可以轻松地把对象装进 Spring 容器里。
5. **使用 DI 依赖注入**：对象存进去了还得互相配合。通过 `@Autowired` 注解自动把我们需要依赖的 Bean “注入”到我们的代码中。


