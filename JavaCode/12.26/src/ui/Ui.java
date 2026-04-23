package ui;

import java.util.Scanner;

public class Ui {
    public int menu(){
        System.out.println("欢迎游玩三子棋");
        System.out.print("请选择难度："+" ");
        System.out.println("1.简单"+" "+"2.困难");
        System.out.println("选择0退出游戏");
        Scanner sc = new Scanner(System.in);
        int buf = 0;
        buf = sc.nextInt();
        return buf;
    }
}
