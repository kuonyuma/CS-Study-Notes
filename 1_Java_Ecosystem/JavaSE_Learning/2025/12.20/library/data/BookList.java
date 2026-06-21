package library.data;

import library.entity.Book;
import java.util.ArrayList;

/**
 * 书架管理类 - 模拟数据库存储
 */
public class BookList {
    private ArrayList<Book> books = new ArrayList<>();

    public BookList() {
        // 初始化一些测试数据
        books.add(new Book("三国演义", "罗贯中", 17.0, "小说"));
        books.add(new Book("西游记", "吴承恩", 47.0, "小说"));
        books.add(new Book("Java编程思想", "Bruce Eckel", 108.0, "技术"));
    }

    /**
     * 获取指定索引的图书
     */
    public Book getBook(int index) {
        if (index >= 0 && index < books.size()) {
            return books.get(index);
        }
        return null;
    }

    /**
     * 替换指定索引的图书
     */
    public void setBook(int index, Book book) {
        if (index >= 0 && index < books.size()) {
            books.set(index, book);
        }
    }

    /**
     * 获取当前图书总数
     */
    public int getSize() {
        return books.size();
    }

    /**
     * 添加图书
     */
    public void addBook(Book book) {
        books.add(book);
    }

    /**
     * 删除指定索引的图书
     */
    public void removeBook(int index) {
        if (index >= 0 && index < books.size()) {
            books.remove(index);
        }
    }

    /**
     * 根据书名查找图书索引
     * 
     * @return 找到返回索引，未找到返回-1
     */
    public int findBookIndexByName(String name) {
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }
}
