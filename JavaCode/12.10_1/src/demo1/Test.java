package demo1;

public class Test {
    public static void main(String[] args){
        Computer tset = new Computer();
        tset.open();

        tset.openDevice(new Mosue());
        tset.openDevice(new Keyboard("dhahah"));

    }
}
