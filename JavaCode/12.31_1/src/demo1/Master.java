package demo1;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

public class Master {
    public static void main(String[] args) {
        ArrayList<Integer> array = new ArrayList<>();
        array.add(10);
        array.add(20);
        array.add(30);
        array.add(40);
        Iterator<Integer> it  = array.iterator();
        ListIterator<Integer> it2 = array.listIterator(array.size());
        while(it.hasNext()){
            System.out.print(it.next()+ " ");
        }
        System.out.println(" ");
        while(it2.hasPrevious()){
            System.out.print(it2.previous()+ " ");
        }


    }
    public static void main1(String[] args) {
        MyArrayList<Integer> arr = new MyArrayList<>();

        arr.add(10);
        arr.add(10);
        arr.add(10);
        arr.add(10);
        arr.clear();
        arr.add(1,999);
        arr.display();
    }
}
