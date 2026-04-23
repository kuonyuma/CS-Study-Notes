package library.user;

import library.operation.*;
import java.util.Scanner;
import java.util.InputMismatchException;

/**
 * 管理员用户类 - 拥有查找、添加、删除、显示图书权限
 */
public class AdminUser extends User {

    public AdminUser(String name) {
        super(name);
        // 初始化管理员的操作权限
        // 注意：数组索引必须与菜单选项对应
        this.operations = new IOperation[] {
                new ExitOperation(), // 0 - 退出
                new FindOperation(), // 1 - 查找
                new AddOperation(), // 2 - 添加
                new DelOperation(), // 3 - 删除
                new ShowOperation() // 4 - 显示所有
        };
    }

    @Override
    public int menu() {
        System.out.println("============================");
        System.out.println("Hello " + this.name + ", 欢迎来到管理员菜单");
        System.out.println("1. 查找图书");
        System.out.println("2. 新增图书");
        System.out.println("3. 删除图书");
        System.out.println("4. 显示所有图书");
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
