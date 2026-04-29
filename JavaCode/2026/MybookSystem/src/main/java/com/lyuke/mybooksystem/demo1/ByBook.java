package com.lyuke.mybooksystem.demo1;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("ByBook")
public class ByBook {

    List<BookInfo> bookList = new ArrayList<>();

    public ByBook() {
        mockData();
    }

    public void mockData () {
        Random random = new Random();
        for (int i = 0; i < 15; i++) {
            BookInfo book = new BookInfo();
            book.setName("图书 " + i);
            book.setAuthor("作者 " + i);
            book.setPrice(BigDecimal.valueOf(random.nextInt(100)));
            book.setType("类型 " + i);
            bookList.add(book);
        }
    }


    //增加图书
    @RequestMapping("/add")
    public List<BookInfo> add(@jakarta.validation.Valid BookInfo book){
        bookList.add(book);
        return bookList;
    }

    //删除
    @RequestMapping("/del")
    public List<BookInfo> del(String name){
        for(BookInfo book : bookList){
            if (name.equals(book.getName())) {
                bookList.remove(book);
                break;
            }
        }
        return bookList;
    }

    //查询图书
    @RequestMapping("/find")
    public BookInfo find(String name){
        for (BookInfo e : bookList) {
            if(e.getName().equals(name)){
                return e;
            }
        }
        return null;
    }

}
