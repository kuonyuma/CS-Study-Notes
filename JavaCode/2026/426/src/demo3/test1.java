package demo3;

class Outer {
    private String name = "Original";

    public void createInner() {
        new Runnable() {
            @Override
            public void run() {
                name = "Changed"; // 直接修改成员变量
                greet();          // 直接调用成员方法
            }
        }.run();
    }

    private void greet() {
        System.out.println("Hello " + name);
    }
}

public class test1{
    public static void main(String[] args) {
        Outer outer = new Outer();
        outer.createInner();
    }
}

