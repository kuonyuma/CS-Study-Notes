import java.util.concurrent.ThreadLocalRandom;

public class Test {

    public static void main_1(String[] args) {

        int num = Solution.findKthLargest(new int[]{3,2,3,1,2,4,5,5,6},4);
        System.out.println(num);
    }

    public static void main(String[] args) {
        int[] arr = Solution.inventoryManagement(new int[]{0,2,3,6},2);
        for(int e : arr)
            System.out.print(e + " ");
    }

}

