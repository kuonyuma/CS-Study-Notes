package demo2;



public class Outer {
    // 1. 外部类静态块
    static {
        System.out.println("【1】外部类静态代码块执行 (Outer Static)");
    }
    int b = 10;

    public Outer() {
        System.out.println("【3】外部类构造方法执行 (Outer Constructor)");
    }

    // 静态内部类 (Static Inner Class)
    static class Inner {

        int a;
        // 2. 内部类静态块
        static {
            System.out.println("【4】内部类静态代码块执行 (Inner Static)");
        }

        public Inner() {
            System.out.println("【5】内部类构造方法执行 (Inner Constructor)");
        }
    }

    public static void main(String[] args) {

        System.out.println("--- main 方法开始 ---");

        // 阶段 A：此时 Outer 已加载，但 Inner 未加载
        new Outer();

        System.out.println("--- 准备使用内部类 ---");

        // 阶段 B：此时才会触发 Inner 加载
        new Inner();

        Inner in = new Inner();
        in.a = 10;


    }
}


