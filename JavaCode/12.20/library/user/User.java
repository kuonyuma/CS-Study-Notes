package library.user;

import library.data.BookList;
import library.operation.IOperation;

/**
 * 抽象用户类 - 定义用户的基本属性和行为
 */
public abstract class User {
    protected String name; // 用户名
    protected IOperation[] operations; // 用户可执行的操作数组

    public User(String name) {
        this.name = name;
    }

    /**
     * 显示菜单并获取用户选择
     * 
     * @return 用户选择的操作编号
     */
    public abstract int menu();

    /**
     * 执行用户选择的操作
     * 
     * @param choice   操作编号
     * @param bookList 书架对象
     */
    public void doOperation(int choice, BookList bookList) {
        if (choice >= 0 && choice < operations.length) {
            operations[choice].work(bookList);
        } else {
            System.out.println("输入非法！请选择正确的操作编号。");
        }
    }

    public String getName() {
        return name;
    }
}
