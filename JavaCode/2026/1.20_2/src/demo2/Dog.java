package demo2;

public class Dog extends Animal implements Iswim{
   public Dog(String name, int age) {
       this.name = name;
       this.age = age;
   }
    public void eat(){
        System.out.println("狗吃骨头");
    }
    public void swim(){
        System.out.println("狗游泳");
    }

    @Override
    public boolean equals(Object obj) {

        if(obj == null){
            return false;
        }
        if(this == obj){
            return true;
        }
        if(!(obj instanceof Dog)){
            return false;

        }else {

            //.操作符号的优先级高于()号
            return name.equals(((Dog) obj).name) && age == ((Dog) obj).age;

        }
    }
    @Override
    public String toString(){
        return "狗的名字是："+name+",年龄是："+age;
    }


}
