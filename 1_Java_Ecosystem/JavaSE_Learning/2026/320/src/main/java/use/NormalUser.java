package use;

import operation.*;

import java.util.Scanner;

public class NormalUser extends User {
    public NormalUser(String name){
        super(name);
    }
    public int menu(){

        System.out.println("0.退出程序");
        System.out.println("1.展示图书");
        System.out.println("2.归还图书");
        System.out.println("3.借出图书");
        System.out.println("4.书名查找");

        System.out.print("请选择功能：");
        Scanner sc = new Scanner(System.in);
        //这里buf就做了接口的下标
        int buf = sc.nextInt();

      //引用需要new对象
        works[0] = new Exit();
        works[1] = new Show();
        works[2] = new Return();
        works[3] = new Borrow();
        works[4] = new Find();
        return buf;
    }
}
