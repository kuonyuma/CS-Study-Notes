package demo1;

public class Test {

    public static void main(String[] args) {
        Computer pc = new Computer();
        pc.open();
        pc.useDevice(new KeyBoard());
        pc.useDevice(new Mouse());
        pc.close();

    }
}
