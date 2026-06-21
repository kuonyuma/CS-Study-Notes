package library.operation;

import library.data.BookList;

/**
 * 显示所有图书操作 - 管理员/用户通用
 */
public class ShowOperation implements IOperation {
    @Override
    public void work(BookList bookList) {
        System.out.println(">>> 显示图书列表");

        if (bookList.getSize() == 0) {
            System.out.println("书架为空，暂无图书。");
            return;
        }

        for (int i = 0; i < bookList.getSize(); i++) {
            System.out.println(bookList.getBook(i));
        }
    }
}
