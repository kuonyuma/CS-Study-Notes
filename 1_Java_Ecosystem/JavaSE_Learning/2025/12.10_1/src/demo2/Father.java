package demo2;

public abstract class Father {
    int money;
    public Father(){
        this.money = 1000;
    }
    public Father(int money){
        this.money = money;
    }
    public abstract void pay();
    public abstract void Sleep();
//        System.out.println("父亲睡觉");

}
