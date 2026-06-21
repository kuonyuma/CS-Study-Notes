package operation;

import book.Book;
import book.BookList;

public class Return implements Operation{
    @Override
    public void work(BookList bookList) {

        //先输入书名
        System.out.println("请输入你要借出的书名");

        Book book = bookList.findBook();
        if(book != null){
            boolean ok = bookList.returnBook(book.getName());
            System.out.println(ok ? "已归还" : "归还失败");
        }else {
            System.out.println("这不是图书管的书");
        }
    }
}
