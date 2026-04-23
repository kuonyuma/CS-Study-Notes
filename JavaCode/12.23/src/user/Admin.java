package user;

public class Admin extends User{

    public Admin(String name){
        super(name);
    }

    public void menu(){
        System.out.println("o.退出系统");
        System.out.println("1.展示图书");
        System.out.println("2.添加图书");
        System.out.println("3.删除图书");
    }
}