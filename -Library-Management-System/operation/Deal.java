package operation;

import book.BookList;
import java.util.Scanner;

public class Deal implements Operation {
    @Override
    public void work(BookList bookList) {

        System.out.println("删除需要书的全称");
        System.out.println("请输入：");
        Scanner sc = new Scanner(System.in);
        String name = sc.nextLine();

        bookList.dealBook(name);

    }

}
