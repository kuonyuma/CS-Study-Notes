package library.operation;

import library.data.BookList;

/**
 * 退出系统操作 - 通用
 */
public class ExitOperation implements IOperation {
    @Override
    public void work(BookList bookList) {
        System.out.println("退出系统，欢迎下次光临！");
        System.exit(0);
    }
}
