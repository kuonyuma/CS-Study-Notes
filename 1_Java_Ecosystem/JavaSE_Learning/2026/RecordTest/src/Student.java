import java.util.Arrays;

public record Student(String name, String email, Integer age){

    public Student{
        if(age < 0)
            throw new IllegalArgumentException("年龄无法为负数");
    }

    @Override
    public boolean equals(Object obj){
        if(obj == null) return false;
        return this.name.equals(((Student) obj).name());
    }

    @Override
    public String name(){
        return name == null ? "无名氏":name;
    }
}
