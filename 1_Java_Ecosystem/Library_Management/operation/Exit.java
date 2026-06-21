package operation;

import book.BookList;

public class Exit implements Operation{
    @Override
    public void work(BookList booList) {
        System.out.println("退出程序");
        System.exit(0);
    }
}
