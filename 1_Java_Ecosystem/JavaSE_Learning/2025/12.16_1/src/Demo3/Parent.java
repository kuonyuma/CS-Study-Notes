package Demo3;

public class Parent {
    // 静态代码块
    static {
        System.out.println("1. 父类静态代码块 (Parent Static Block)");
    }

    // 实例代码块
    {
        System.out.println("3. 父类实例代码块 (Parent Instance Block)");
    }

    // 构造函数
    public Parent() {
        System.out.println("4. 父类构造函数 (Parent Constructor)");
    }
}

