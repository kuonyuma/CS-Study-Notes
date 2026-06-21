package book;

public class Book {
    private String bookname;
    private String Author;
    private int price;
    private String type;
    private Boolean be;

    public Book(String bookname,String Author,int price,String type){
        this.bookname = bookname;
        this.Author = Author;
        this.price = price;
        this.type = type;
    }
    public void Author(String Author){
        this.Author = Author;
    }
    public String Author(){
        return this.Author;
    }
    public void setPrice(int price){
        this.price = price;
    }
    public int getPrice(){
        return this.price;
    }
    public void setType(String type){
        this.type = type;
    }
    public String getType(){
        return this.type;
    }

}
