package demo1;

public class Keyboard implements USB {
    String name;
    public void open(){
        System.out.println("链接键盘");
    }
    public void close(){
        System.out.println("关闭键盘");
    }
    public Keyboard(String name){
        this.name = name;
    }
    void setInput(String name){
        this.name = name;
    }
    String outInput(){
        return this.name;
    }

}
