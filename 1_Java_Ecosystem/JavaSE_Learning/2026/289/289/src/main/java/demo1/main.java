package demo1;

import demo1.user.Student;

import java.util.ArrayList;

public class main {

    public static void main(String[] args) {

        try{
            Student s2 = new Student("ryuke",12);
            s2.setList(new ArrayList<>());
            for(int i = 0;i < 10;i++){
                s2.list.add(i);
            }

            Student s3 = s2.clone();
            System.out.println(s3.name);
            System.out.println(s3.getAge());
            System.out.println(s3.list == s2.list);

        }catch (CloneNotSupportedException e){
            System.out.println("克隆异常");
        }



    }
}
