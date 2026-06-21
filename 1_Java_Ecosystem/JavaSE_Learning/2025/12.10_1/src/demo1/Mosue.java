package demo1;

public class Mosue implements USB {

    public void open(){
        System.out.println("链接鼠标");
    }
    public void close(){
        System.out.println("断开鼠标");
    }
    public void click(){
        System.out.println("点击鼠标");
    }
}
