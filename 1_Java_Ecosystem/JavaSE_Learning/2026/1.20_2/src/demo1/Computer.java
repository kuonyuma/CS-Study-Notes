package demo1;

public class Computer {
    public void open(){
        System.out.println("打开电脑");
    }

    public void useDevice(USB usb){
        usb.openDevice();
        if(usb instanceof Mouse){
            Mouse mouse = (Mouse) usb;
            mouse.click();
        }else if(usb instanceof KeyBoard){
            KeyBoard keyBoard = (KeyBoard) usb;
            keyBoard.input();
        }
        usb.closeDevice();
    }

    public void close(){
        System.out.println("关闭电脑");
    }
}
