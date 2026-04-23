package library.user;

import library.operation.*;
import java.util.Scanner;
import java.util.InputMismatchException;

/**
 * 普通用户类 - 拥有查找、借阅、归还图书权限
 */
public class NormalUser extends User {

    public NormalUser(String name) {
        super(name);
        // 初始化普通用户的操作权限
        // 注意：数组索引必须与菜单选项对应
        this.operations = new IOperation[] {
                new ExitOperation(), // 0 - 退出
                new FindOperation(), // 1 - 查找
                new BorrowOperation(), // 2 - 借阅
                new ReturnOperation() // 3 - 归还
        };
    }

    @Override
    public int menu() {
        System.out.println("============================");
        System.out.println("Hello " + this.name + ", 欢迎来到普通用户菜单");
        System.out.println("1. 查找图书");
        System.out.println("2. 借阅图书");
        System.out.println("3. 归还图书");
        System.out.println("0. 退出系统");
        System.out.println("============================");
        System.out.print("请输入您的操作: ");

        Scanner scanner = new Scanner(System.in);
        try {
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            return -1; // 返回非法值，让 doOperation 处理
        }
    }
}
