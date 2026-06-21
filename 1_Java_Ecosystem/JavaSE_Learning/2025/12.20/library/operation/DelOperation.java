package library.operation;

import library.data.BookList;
import java.util.Scanner;

/**
 * 删除图书操作 - 管理员专用
 */
public class DelOperation implements IOperation {
    @Override
    public void work(BookList bookList) {
        System.out.println(">>> 删除图书");
        Scanner scanner = new Scanner(System.in);

        System.out.print("请输入要删除的图书名字: ");
        String name = scanner.nextLine();

        int index = bookList.findBookIndexByName(name);
        if (index == -1) {
            System.out.println("没有找到这本书，无法删除。");
            return;
        }

        bookList.removeBook(index);
        System.out.println("删除成功！");
    }
}
