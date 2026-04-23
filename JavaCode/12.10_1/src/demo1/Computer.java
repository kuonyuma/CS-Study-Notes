package demo1;

public class Computer  {

    public void open(){
        System.out.println("开机");
    }
    public void close(){
        System.out.println("关机");
    }
    public void openDevice(USB sub){
        if(sub instanceof Keyboard){
          Keyboard k =(Keyboard) sub;
            k.open();
        }else if(sub instanceof Mosue){
            Mosue m = (Mosue) sub;
            m.open();
        }
    }

}
