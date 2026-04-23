package operation;

import book.Book;
import book.BookList;

public class Find implements Operation{
    @Override
    public void work(BookList bookList) {

        System.out.println("请输入你要查找的书名");
        bookList.searchBooks();

    }
}
