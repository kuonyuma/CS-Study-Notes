package demo3;

import java.util.Arrays;

public class Test {
    //接口的实践
    /*
    前置知识回顾：
    Object类是所有类的父类，所有类都直接或间接继承Object类
    继承父类的子类可以直接调用父类的方法
    Object类中有一个equals方法，用于比较两个对象是否相等，默认比较的是地址值
    如果想要比较对象的属性值，需要重写equals方法
    ==============================================================
    关于接口的使用，主要体现在两个方面：
    1. 比较两个对象是否相等，重写Object类中的equals方法
    2. 比较两个对象的大小，使用Comparable接口或者Comparator接口
     */
    public static void mySort(Student[] students) {
        for (int i = 0; i < students.length - 1; i++) {
            for (int j = 0; j < students.length - 1 - i; j++) {
                if (students[j].compareTo(students[j + 1]) > 0) {
                    Student temp = students[j];
                    students[j] = students[j + 1];
                    students[j + 1] = temp;
                }
            }
        }
    }
    public static void main(String[] args) {
        Student stu = new Student(20, "张三");
        Student stu1 = new Student(22, "李四");
        //这里比较的是两个对象的地址值
        System.out.println(stu == stu1);
        /*如果想要比较对象的属性值，需要重写equals方法
        这里的eauals方法是Object类中的方法，默认比较的是地址值
        public boolean equals(Object obj) {
            return (this == obj);
        }*/
        System.out.println(stu.equals(stu1));
        System.out.println("-------------------");
        Student stu3 = new Student(20, "张三");
        System.out.println(stu3.equals(stu));

        /*
        问题：需要比较的对象很多怎么办？
        将比较对象放入数组
        此时可以引入一个比较器接口Comparator
         */
        Student[] students = new Student[3];
        students[0] = new Student(23, "王五");
        students[1] = new Student(19, "赵六");
        students[2] = new Student(21, "钱七");
        //这个比较器是我自己定义的，按年龄升序排序
        Arrays.sort(students, new MyComparator());
        for (Student student : students) {
            System.out.println(student.name + "----" + student.age);
        }
        System.out.println("-------------------");
        //这个比较器是我们自己定义的，按姓名字典顺序排序
        Arrays.sort(students,new AgeComparator());
        for (Student student : students) {
            System.out.println(student.name + "----" + student.age);
        }
        //其实也可以不使用比较器，直接在Student类中实现Comparable接口
        System.out.println("-------------------");
        //这里没有比较器，直接使用Student类中的compareTo方法，按年龄升序排序
        //因为sort方法如果没有传比较器，会自动调用compareTo方法
        Arrays.sort(students);
        for (Student student : students) {
            System.out.println(student.name + "----" + student.age);
        }

        System.out.println("-------------------");
        mySort(students);
        //如果想实现倒叙排序，可以在compareTo方法中修改比较逻辑
        //也可以在mySort方法中修改比较逻辑
        for (Student student : students) {
            System.out.println(student.name + "----" + student.age);
        }
    }
}
