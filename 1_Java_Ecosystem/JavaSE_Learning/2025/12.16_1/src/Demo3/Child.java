package Demo3;

public class Child extends Parent {
    // 静态代码块
    static {
        System.out.println("2. 子类静态代码块 (Child Static Block)");
    }

    // 实例代码块
    {
        System.out.println("5. 子类实例代码块 (Child Instance Block)");
    }

    // 构造函数
    public Child() {
        System.out.println("6. 子类构造函数 (Child Constructor)");
    }
}

