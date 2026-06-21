import java.util.Random;
import java.util.function.Consumer;

public class Test {

    public static int[] initArray(){
        return initRandomArray(40000,400000);
    }

    public static int[] reversedArr() {
        int[] arr = new int[40000];
        for(int i = 0; i < arr.length; i++){
            arr[i] = arr.length - i; // 生成倒序数组
        }
        return arr;
    }

    public static int[] initRandomArray(int n, int bound) {
        Random rnd = new Random();
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = rnd.nextInt(bound); //生成 [0, bound) 的随机数 }
        }
        return arr;
    }

    /**
     * 通用的排序测试方法
     * @param sortName 排序算法名称
     * @param sorter   排序算法函数引用
     */
    public static void testSort(String sortName, Consumer<int[]> sorter) {
        int[] arr = initArray();
        long time1 = System.currentTimeMillis();
        sorter.accept(arr);
        long time2 = System.currentTimeMillis();
        System.out.println(sortName + "的时间为：" + (time2 - time1) + "毫秒");
    }

    public static void test(){
        int[] arr = {100,31,2,6,5,3,15,1111};
        Sort.countingSort(arr);
        for(int e : arr){
            System.out.print(e+" ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        test();
        System.out.println("--- 开始性能测试 ---");
        main1(args);
    }

    public static void main1(String[] args) {
        testSort("直接插入排序", Sort::insertSort);
        System.out.println("==========================");

        testSort("希尔排序", Sort::shellSort);
        System.out.println("==============================");

        testSort("选择排序", Sort::selectSort);
        System.out.println("=============================");

        testSort("堆排序", Sort::heapSort);
        System.out.println("===============================");

        testSort("快速排序", Sort::quickSort);
        System.out.println("===============================");

        testSort("归并排序", Sort::MergeSort);
    }
}