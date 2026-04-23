package library.operation;

import library.data.BookList;

/**
 * 操作接口 - 定义所有操作的标准
 */
public interface IOperation {
    /**
     * 执行操作
     * 
     * @param bookList 书架对象
     */
    void work(BookList bookList);
}
