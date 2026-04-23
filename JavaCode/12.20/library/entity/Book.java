package library.entity;

/**
 * 图书实体类 - 封装图书信息
 */
public class Book {
    private String name; // 书名
    private String author; // 作者
    private double price; // 价格
    private String type; // 类型
    private boolean isBorrowed; // true表示已借出，false表示未借出

    public Book(String name, String author, double price, String type) {
        this.name = name;
        this.author = author;
        this.price = price;
        this.type = type;
        this.isBorrowed = false; // 默认在馆
    }

    // Getter 和 Setter 方法
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isBorrowed() {
        return isBorrowed;
    }

    public void setBorrowed(boolean borrowed) {
        isBorrowed = borrowed;
    }

    @Override
    public String toString() {
        return "书名:《" + name + "》" +
                "\t作者:" + author +
                "\t价格:" + price +
                "\t类型:" + type +
                "\t状态:" + (isBorrowed ? "【已借出】" : "【在馆】");
    }
}
