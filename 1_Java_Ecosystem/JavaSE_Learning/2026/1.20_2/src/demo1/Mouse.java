package demo1;

public class Mouse implements USB {
    public void click() {
        System.out.println("鼠标点击");
    }
    @Override
    public void openDevice() {
        System.out.println("打开鼠标");
    }
    @Override
    public void closeDevice() {
        System.out.println("关闭鼠标");
    }

}
