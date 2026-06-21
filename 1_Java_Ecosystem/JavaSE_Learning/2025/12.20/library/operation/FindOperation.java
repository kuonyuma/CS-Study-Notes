package library.operation;

import library.data.BookList;
import java.util.Scanner;

/**
 * 查找图书操作 - 管理员/用户通用
 */
public class FindOperation implements IOperation {
    @Override
    public void work(BookList bookList) {
        System.out.println(">>> 查找图书");
        Scanner scanner = new Scanner(System.in);

        System.out.print("请输入要查找的图书名字: ");
        String name = scanner.nextLine();

        int index = bookList.findBookIndexByName(name);
        if (index == -1) {
            System.out.println("未找到此书。");
        } else {
            System.out.println("找到了，信息如下: ");
            System.out.println(bookList.getBook(index));
        }
    }
}
