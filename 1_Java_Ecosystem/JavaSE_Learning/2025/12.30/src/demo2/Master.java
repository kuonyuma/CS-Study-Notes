package demo2;


 class Test<T extends Comparable<T>>{
     public  static <T extends Comparable<T>> T findMax(T[] array){
        T max = array[0];
        for (int i = 0; i < array.length; i++) {
            if(max.compareTo(array[i])<0){
                max = array[i];
            }
        }
        return max;
    }

}

public class Master{
    public static void main(String[] args) {
        Integer[] arr = {1,23,4,5,6,7,8,9};

        System.out.println(Test.findMax(arr));

    }

}