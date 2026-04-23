package user;

import board.initBoard;

import java.util.Scanner;

import static board.initBoard.USER;

public class Player {
    public void set(initBoard board){
        int buf = 0;
        Scanner sc = new Scanner(System.in);

        System.out.println("请输入你要下棋的位置");
        System.out.print(": ");

        // 检查用户输入的第一个内容是不是整数
        if (sc.hasNextInt()) {
            int x = sc.nextInt();
            int y = sc.nextInt();
            if(x>2||x<0){
                System.out.println("输入不合法");
            }
            if(y>2||y<0){
                System.out.println("输入不合法");
            }
            if(board.getBoard(x,y)==' '){
                board.setBoard(x,y,USER);
            }
        }
    }

}
