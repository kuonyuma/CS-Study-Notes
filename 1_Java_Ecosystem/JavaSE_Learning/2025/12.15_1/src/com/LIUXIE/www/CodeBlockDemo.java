package com.LIUXIE.www;

public class CodeBlockDemo {
    private int num1;
    private static int num2;

    //实例代码块
    {
        num1 =1;

    }
    //静态代码块
    static{
        num2 = 2;
    }
    //构造方法
    public CodeBlockDemo(int num1,int num2){
        num1 = 11;
        num2 = 12;
    }
    public void show(){
        System.out.println(this.num1+ " "+ this.num2);
    }

}
