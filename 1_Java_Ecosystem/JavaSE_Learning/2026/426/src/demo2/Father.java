package demo2;

public class Father {

    private static String schoolName = "清华大学";
    private String name;

    public Father(String name) {
        this.name = name;
    }

    public static void print(){
        System.out.println("来自父类的print() " + schoolName);
    }

    // 静态内部类 (Static Inner Class)
    // 1. 静态内部类可以直接访问外部类的静态成员（包括私有的）
    // 2. 静态内部类不可以直接访问外部类的非静态成员
    // 3. 静态内部类的实例化不需要依赖外部类对象
    public static class StaticInner {
        private String innerName;

        public StaticInner(String innerName) {
            this.innerName = innerName;
        }
        public StaticInner(){

        }

        public void show() {
            System.out.println("我是静态内部类：" + innerName);
            // 直接访问外部类的静态属性
            System.out.println("可以访问外部类的静态属性：" + schoolName);
            
            // 注意：这里无法直接访问外部类的实例属性 name，除非通过外部类实例
            // System.out.println(name); // 编译报错

            //方式1：
            print();
            //方式2：
            Father.print();
        }
    }

    public static void main(String[] args) {
        // 静态内部类的创建方式：外部类名.内部类名 变量名 = new 外部类名.内部类提示();
        Father.StaticInner inner = new Father.StaticInner("三叶");
        inner.show();
    }
}
