package library.operation;

import library.data.BookList;
import library.entity.Book;
import java.util.Scanner;

/**
 * 归还图书操作 - 用户专用
 */
public class ReturnOperation implements IOperation {
    @Override
    public void work(BookList bookList) {
        System.out.println(">>> 归还图书");
        Scanner scanner = new Scanner(System.in);

        System.out.print("请输入要归还的图书名字: ");
        String name = scanner.nextLine();

        int index = bookList.findBookIndexByName(name);
        if (index == -1) {
            System.out.println("没有找到这本书，无需归还。");
            return;
        }

        Book book = bookList.getBook(index);
        if (book.isBorrowed()) {
            book.setBorrowed(false);
            System.out.println("归还成功！");
        } else {
            System.out.println("这本书在馆内，无需归还。");
        }
    }
}
