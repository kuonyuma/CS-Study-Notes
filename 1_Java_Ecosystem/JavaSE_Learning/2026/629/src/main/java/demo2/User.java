package demo2;

public class User {

    @MyRange(min = 18, max = 60)
    Integer age;

    public User(Integer age){
        this.age = age;
    }

    public Integer getAge(){
        return age;
    }

    public static void main(String[] args) {
        User user = new User(29);
        Validator.validate(user);
    }

}
