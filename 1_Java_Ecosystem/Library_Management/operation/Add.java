package operation;

import book.BookList;
import book.Book;
import java.util.Scanner;

public class Add implements Operation{
    @Override
    public void work(BookList bookList) {

        System.out.println("请输入图书的信息");
        Scanner sc = new Scanner(System.in);
        System.out.println("书名");
        String name = sc.nextLine();
        Book book = bookList.findBookByTitle(name);
        if(book != null){
            bookList.addBook(new Book(book.getName(), book.getAuthor(), book.getType(), book.getPrice()));
            System.out.println("图书增加成功");
            return;
        }
        System.out.println("作者");
        String author = sc.nextLine();
        System.out.println("类型");
        String type = sc.nextLine();
        System.out.println("价格");
        int price = sc.nextInt();

        book = new Book(name,author,type,price);

        bookList.addBook(book);

    }
}
