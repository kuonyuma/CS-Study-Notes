package operation;

import book.Book;
import book.BookList;

public class Borrow implements Operation{
    @Override
    public void work(BookList bookList) {

        System.out.println("请输入你要借出的书名");
        Book book = bookList.findBook();
        if(book != null && book.getCount() != 0){
            boolean ok = bookList.borrowBook(book.getName());
            System.out.println(ok ? "成功借阅" : "借阅失败");
        } else if(book != null && book.getCount() == 0){
            System.out.println("该书已经借完");
        }else{
            System.out.println("图书馆没有该书");
        }
    }
}
