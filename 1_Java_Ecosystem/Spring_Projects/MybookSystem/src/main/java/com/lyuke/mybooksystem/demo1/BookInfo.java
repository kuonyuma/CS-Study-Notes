package com.lyuke.mybooksystem.demo1;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Objects;

@Data
public class BookInfo {
    private String name;
    private String author;
    private BigDecimal Price;
    private String type;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BookInfo bookInfo = (BookInfo) o;
        return bookInfo.name.equals(this.name);
        Thread
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, author, Price, type);
    }
}
