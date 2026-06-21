package com.lyuke.booksystem.controller;

import com.lyuke.booksystem.model.BookInfo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/book")
public class BookController {

    private static List<BookInfo> books = new ArrayList<>();
    private static int nextId = 1;

    static {
        for (int i = 1; i <= 5; i++) {
            BookInfo book = new BookInfo();
            book.setId(nextId++);
            book.setBookName("书籍" + i);
            book.setAuthor("作者" + i);
            book.setCount(i * 5 + 3);
            book.setPrice(new BigDecimal(10 * i + 9));
            book.setPublish("出版社" + i);
            book.setStatus(1);
            books.add(book);
        }
    }

    @RequestMapping("/getList")
    public List<BookInfo> getList() {
        for (BookInfo book : books) {
            if (book.getStatus() != null && book.getStatus() == 1) {
                book.setStatusCN("可借阅");
            } else {
                book.setStatusCN("不可借阅");
            }
        }
        return books;
    }

    @RequestMapping("/add")
    public boolean add(BookInfo bookInfo) {
        try {
            bookInfo.setId(nextId++);
            if (bookInfo.getStatus() == null) {
                bookInfo.setStatus(1); // 默认可借阅
            }
            books.add(bookInfo);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @RequestMapping("/getById")
    public BookInfo getById(Integer bookId) {
        for (BookInfo book : books) {
            if (book.getId().equals(bookId)) {
                return book;
            }
        }
        return null;
    }

    @RequestMapping("/update")
    public boolean update(BookInfo bookInfo) {
        for (int i = 0; i < books.size(); i++) {
            BookInfo book = books.get(i);
            if (book.getId().equals(bookInfo.getId())) {
                books.set(i, bookInfo);
                return true;
            }
        }
        return false;
    }

    @RequestMapping("/delete")
    public boolean delete(Integer bookId) {
        Iterator<BookInfo> iterator = books.iterator();
        while (iterator.hasNext()) {
            BookInfo book = iterator.next();
            if (book.getId().equals(bookId)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    @RequestMapping("/batchDelete")
    public boolean batchDelete(@RequestParam("ids[]") List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        boolean deleted = false;
        Iterator<BookInfo> iterator = books.iterator();
        while (iterator.hasNext()) {
            BookInfo book = iterator.next();
            if (ids.contains(book.getId())) {
                iterator.remove();
                deleted = true;
            }
        }
        return deleted;
    }
}
