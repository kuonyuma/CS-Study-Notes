package demo3.db;

public class Child extends Parent{

    int val = 100;

    public Child(){
        show();
    }

    @Override
    public void show(){
        System.out.println(val);
    }
//    @Override
//    public void eat(){
//        System.out.println("");
//    }
}
