package Demo1;

public class Animal {
    public String name;
    public int age;
    public int a;

    public Animal(){
    }
    public Animal(String name, int age){
        this.name = name;
        this.age = age;
    }
    public void eat(){
        System.out.println(this.name+"正在吃");
    }

}
