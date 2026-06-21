package library.operation;

import library.data.BookList;
import library.entity.Book;
import java.util.Scanner;

/**
 * 添加图书操作 - 管理员专用
 */
public class AddOperation implements IOperation {
    @Override
    public void work(BookList bookList) {
        System.out.println(">>> 添加图书");
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("请输入图书名字: ");
            String name = scanner.nextLine();

            System.out.print("请输入图书作者: ");
            String author = scanner.nextLine();

            System.out.print("请输入图书类型: ");
            String type = scanner.nextLine();

            System.out.print("请输入图书价格: ");
            double price = Double.parseDouble(scanner.nextLine());

            if (price < 0) {
                System.out.println("价格不能为负数！");
                return;
            }

            Book book = new Book(name, author, price, type);
            bookList.addBook(book);
            System.out.println("添加成功！");
        } catch (NumberFormatException e) {
            System.out.println("价格输入格式错误！");
        }
    }
}
