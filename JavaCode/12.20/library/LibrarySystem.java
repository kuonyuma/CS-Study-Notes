package library;

import library.data.BookList;
import library.user.*;
import java.util.Scanner;
import java.util.InputMismatchException;

/**
 * 图书管理系统 - 主程序入口
 * 
 * 功能说明:
 * - 管理员: 查找、添加、删除、显示所有图书
 * - 普通用户: 查找、借阅、归还图书
 */
public class LibrarySystem {

    /**
     * 用户登录
     * 
     * @return 登录成功的用户对象
     */
    public static User login() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=====================================");
        System.out.println("      欢迎使用图书管理系统");
        System.out.println("=====================================");

        System.out.print("请输入您的姓名: ");
        String name = scanner.nextLine();

        System.out.println("请选择您的身份:");
        System.out.println("  1. 管理员");
        System.out.println("  2. 普通用户");
        System.out.print("请选择: ");

        try {
            int choice = scanner.nextInt();
            if (choice == 1) {
                return new AdminUser(name);
            } else {
                return new NormalUser(name);
            }
        } catch (InputMismatchException e) {
            System.out.println("输入无效，默认以普通用户身份登录。");
            return new NormalUser(name);
        }
    }

    /**
     * 主程序入口
     */
    public static void main(String[] args) {
        // 1. 准备书籍数据（模拟数据库）
        BookList bookList = new BookList();

        // 2. 用户登录
        User user = login();
        System.out.println("\n登录成功！");

        // 3. 进入主循环
        while (true) {
            // 获取用户选择
            int choice = user.menu();

            // 根据选择执行对应的操作（多态的体现）
            user.doOperation(choice, bookList);

            // 为了视觉效果，暂停一下
            System.out.println("\n按回车键继续...");
            try {
                // 清理可能的多余输入
                System.in.read();
            } catch (Exception e) {
                // 忽略异常
            }
        }
    }
}
