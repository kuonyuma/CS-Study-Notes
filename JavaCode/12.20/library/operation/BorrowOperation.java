package library.operation;

import library.data.BookList;
import library.entity.Book;
import java.util.Scanner;

/**
 * 借阅图书操作 - 用户专用
 */
public class BorrowOperation implements IOperation {
    @Override
    public void work(BookList bookList) {
        System.out.println(">>> 借阅图书");
        Scanner scanner = new Scanner(System.in);

        System.out.print("请输入要借阅的图书名字: ");
        String name = scanner.nextLine();

        int index = bookList.findBookIndexByName(name);
        if (index == -1) {
            System.out.println("没有找到这本书。");
            return;
        }

        Book book = bookList.getBook(index);
        if (book.isBorrowed()) {
            System.out.println("这本书已经被借走了！");
        } else {
            book.setBorrowed(true);
            System.out.println("借阅成功！");
        }
    }
}
