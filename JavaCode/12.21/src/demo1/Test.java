package demo1;

import java.util.Arrays;
import java.util.Comparator;
public class Test {
    public static void mySort(Student[] Stu){

        for (int i = 0; i <Stu.length-1 ; i++) {
            for (int j = 0; j < Stu.length -i -1; j++) {
                if(Stu[j].compareTo(Stu[j+1])>0){
                    Student temp = Stu[j];
                    Stu[j] = Stu[j+1];
                    Stu[j+1] = temp ;
                }
            }
        }
    }

    public static void main(String[] args) {
        Student[] Stu1=new Student[3];

        Stu1[0] = new Student(1100,"asomasaku");
        Stu1[1] = new Student (19,"bliuxie");
        Stu1[2] = new Student (1,"aakuonyuma");


        System.out.println("==================================");
        AgeCompara judge = new AgeCompara();
        NameComparator judge1 = new NameComparator();
        //Test.mySort(Stu1);
        Arrays.sort(Stu1,judge1);
        for (int i = 0; i < Stu1.length; i++) {
            System.out.println(Stu1[i]);
        }

    }
}
