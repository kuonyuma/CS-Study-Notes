package demo3;

import java.util.Comparator;

public class AgeComparator implements Comparator<Student> {
    @Override
    public int compare(Student stu1, Student stu2) {
       return stu1.name.compareTo(stu2.name);
    }
}
