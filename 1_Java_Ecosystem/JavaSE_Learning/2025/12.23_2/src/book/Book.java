package book;


import java.util.Objects;

//图书管理系统一定得有书
public class Book {
    private String name;//书名
    private String author;
    private String type;
    private int price;//价格
    private int count;//相同书的数量

    public Book(String name,String author,String type,int price){
        this.name = name;
        this.author = author;
        this.type = type;
        this.price = price;
        this.count = 1;

    }
    public Book() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setCount(int count){
        this.count += count;
    }
    public void resetCount(int count) {
        this.count = count;
    }
    public int getCount( ){
        return count;
    }

    @Override
    public String toString() {
        return "book{" +
                "name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", type='" + type + '\'' +
                ", price=" + price +
                ", count=" + (count == 0 ? "已经结完" : count ) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return price == book.price &&  Objects.equals(name, book.name) && Objects.equals(author, book.author) && Objects.equals(type, book.type);
    }
    @Override
    public int hashCode() {
        return Objects.hash(name, author, type, price);
    }
}
