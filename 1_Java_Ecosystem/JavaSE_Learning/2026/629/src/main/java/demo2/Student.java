package demo2;

public class Student {
    private String name;
    private String email;

    private Student(String name){
        this.name = name;
    }

    private void say(){
        System.out.println(this.name +"正在说话");
    }
    private static void sleep(){
        System.out.println("正在睡觉");
    }


    @Deprecated//练习注解，无实际意义
    public String getName() {
        return name;
    }

    public String getEmail(){
        return email;
    }

}
