package demo3;

public class User {


    private Integer age;
    private String name;

    private User(Integer age,String name){
        this.age =age;
        this.name =name;
    }

    private void info(){
        System.out.println("姓名:"+name+" 年龄: "+ age);
    }
    public Integer getAge(){
        return age;
    }
    public String getName(){
        return name;
    }
}
