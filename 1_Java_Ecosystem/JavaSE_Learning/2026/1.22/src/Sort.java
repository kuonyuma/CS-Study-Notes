import java.util.Arrays;
import java.util.Stack;

public class Sort {

    /**
     * 直接插入排序：
     * 时间复杂度是O(n^2)
     * 稳定排序
     * @param arr
     */
    public static void insertSort(int[] arr){

        for(int i = 1;i < arr.length;i++){
            int tmp = arr[i];
            int j = i - 1 ;
            for(;j >= 0; j--){
                if(tmp < arr[j]){
                    arr[j + 1] = arr[j];
                }else{
                    break;
                }
            }
            arr[j + 1] =  tmp;
        }

    }
    /**
     * 希尔排序
     * 时间复杂度O(n^1.3)~O(n^2)
     * 不稳定排序
     * @param arr
     */

    public static void shellSort(int[] arr){
        int gap = arr.length;
        while(gap >= 1){
            gap = gap / 2;
            shell(arr,gap);
        }
    }
    /**
     * 希尔排序的子过程：对每组间隔为 gap 的元素进行直接插入排序
     * @param arr 待排序数组
     * @param gap 分组间隔
     */
    private static void shell(int[] arr,int gap){
        for (int i = gap; i < arr.length; i++) {
            int tmp = arr[i];
            int j = i - gap;
            for(;j >= 0;j -= gap){
                if(tmp < arr[j]){
                    arr[j + gap] = arr[j];
                }else{
                    break;
                }
            }
            arr[j + gap] = tmp;
        }
    }
    /**
     * 选择排序
     * 时间复杂度O(n^2)
     * 不稳定排序
     *
     * 这个排序就是从数组的1号位开始一直遍历到最后，在遍历的途中记录一下，最大值和最小值的下表。
     * 一轮遍历结束以后就，将最小值给到0号，最大数值给到最后一位。
     * 每一轮结束后左指针++，右指针--
     * @param arr
     */

    public static void selectSort(int[] arr){
        int left = 0;
        int right = arr.length - 1;
        while(left < right){
            int minIndex = left;
            int maxIndex = left;
            for(int i = left + 1;i <= right;i++ ){
                if(arr[i] < arr[minIndex]){
                    minIndex = i;
                }
                if(arr[i] > arr[maxIndex]){
                    maxIndex = i;
                }
            }
            swap(arr,left,minIndex);
            if(maxIndex == left){
              maxIndex = minIndex;
            }
            swap(arr,right,maxIndex);
            left++;
            right--;
        }
    }
    /**
     * 交换数组中两个元素的位置
     * @param arr 数组
     * @param i 下标 i
     * @param j 下标 j
     */
    private static void swap(int[]arr,int i,int j ){
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }
    /**
     *堆排
     * @param arr
     *
     * 首先将无序的数组创建成一个大根堆，然后将根节点弹出到最后，同时数组长度--。
     */
    public static void heapSort(int[] arr){
        if(arr.length == 0){
            return;
        }
        creatHeap(arr);
        int end = arr.length -1;
        while(end > 0){
            swap(arr,0,end--);
            siftDown(arr, end,0);
        }


    }
    /**
     * 建堆（大根堆）
     * 从最后一个非叶子节点开始，依次向下调整
     * @param arr 待建堆数组
     */
    private static void creatHeap(int[]arr){
        for (int i = (arr.length - 2)/ 2; i >= 0 ; i--) {
            siftDown(arr,arr.length,i);
        }
    }

    /**
     * 向下调整算法
     * 保证 parent 节点大于其子节点
     * @param arr 数组
     * @param size 调整范围（有效数据个数）
     * @param parent 当前需要调整的父节点下标
     */
    private static void siftDown(int[] arr,int size,int parent){
        int child = parent*2 + 1;
        while(child <size){
            if(child+1 < size && arr[child]<arr[child+1]){
                    child +=1;
            }
            if(arr[child] > arr[parent]) {
                swap(arr, parent, child);
            }
                parent = child;
                child = child*2 +1;

        }
    }
    /**
     * 快速排序
     *
     * 时间复杂度：
     * 一般情况下为O（N*logN）
     * 不一般：O（N^2），数据为有序的情况下。
     *
     * 空间复杂度:
     * 极限是O（N）
     * 一般是O(logN)
     *
     * 稳定性：不稳定的
     * @param arr
     *
     *
     * 两个指针分别从最左端与最右端开始向中间走，右指针先动，以左值针的值为基准
     *右指针指向的数值如果小于基准，就将该值赋给左指针指向的位置，
     * 左指针则反之，
     * 两指针相遇后，将基准值赋给相遇地点，
     * 再将该数组分成两半，两指针相遇点为切割线。
     */
    public static void quickSort(int[] arr){
        if(arr.length == 0){
            return;
        }
        quickNor(arr,0,arr.length-1);
    }
    /**
     * 非递归实现快速排序
     * 利用栈模拟递归过程
     * @param arr 数组
     * @param left 左边界
     * @param right 右边界
     */
    private static void quickNor(int[] arr, int left,int right){
        Stack<Integer> stack = new Stack<>();
        int pivot = overwriteSort(arr,left,right);
        if(left + 1 < pivot){
            stack.push(left);
            stack.push(pivot - 1);
        }
        if(pivot < right - 1){
            stack.push(pivot + 1);
            stack.push(right);
        }
        while(!stack.isEmpty()){
            right = stack.pop();
            left = stack.pop();
            int curPivot = overwriteSort(arr,left,right);
            if(left + 1 < curPivot){
                stack.push(left);
                stack.push(curPivot - 1);
            }
            if(curPivot < right - 1){
                stack.push(curPivot + 1);
                stack.push(right);
            }
        }
    }

    /**
     * 递归实现快速排序
     * 当区间长度较小时改用插入排序优化
     * @param arr 数组
     * @param left 左边界
     * @param right 右边界
     */
    private static void quick(int[] arr, int left,int right){
        if(left >= right){
            return;
        }
        if(right + 1 - left <= 5){
            insertSort(arr,left,right);
            return;
        }
        //三数取中法
        int key = getMindIndex(arr,left,right);
        swap(arr,key,left);

        int pos = overwriteSort(arr,left,right);

        quick(arr,left,pos - 1);
        quick(arr,pos + 1,right);

    }
    /**
     * 指定区间的直接插入排序
     * 用于快速排序的小区间优化
     * @param arr 数组
     * @param left 左边界
     * @param right 右边界
     */
    public static void insertSort(int[] arr,int left, int right){

        for(int i = left + 1;i <= right;i++){
            int tmp = arr[i];
            int j = i - 1 ;
            for(;j >= left; j--){
                if(tmp < arr[j]){
                    arr[j + 1] = arr[j];
                }else{
                    break;
                }
            }
            arr[j + 1] =  tmp;
        }

    }

    /**
     * 三数取中法
     * 选取 left, right, mid 三者中间值的下标
     * @param arr 数组
     * @param left 左边界
     * @param right 右边界
     * @return 中间值的下标
     */
    private static int getMindIndex(int[] arr,int left ,int right ){
        int mindIndex = (left + right) / 2;
        if(arr[left] < arr[right]){
            if(arr[left] > arr[mindIndex]){
                return left;
            } else if (arr[right]< arr[mindIndex]) {
                return right;
            }else{
                return mindIndex;
            }
        }else{
            if(arr[right] > arr[mindIndex]){
                return right;
            } else if (arr[left]< arr[mindIndex]) {
                return left;
            }else{
                return mindIndex;
            }
        }
    }

    /**
     * Hoare 分区法
     * @param arr 数组
     * @param start 起始下标
     * @param end 结束下标
     * @return 基准值最终位置
     */
    private static int HoareSort(int[] arr,int start, int end) {
        int tmp = arr[start];
        int bufindex = start;
        while (start < end) {
            while (end > start && arr[end] >= tmp) {
                end--;
            }
            while (start < end && arr[start] <= tmp) {
                start++;
            }
            if(start < end){
                swap(arr,end,start);
            }
        }
        swap(arr, bufindex, start);
        return start;
    }
    /**
     * 挖坑法（Overwrite Sort）
     * @param arr 数组
     * @param start 起始下标
     * @param end 结束下标
     * @return 基准值最终位置
     */
    private static int overwriteSort(int[] arr,int start, int end) {
        int tmp = arr[start];
        int bufindex = start;
        while(start < end){
            while(end > start && arr[end] >= tmp){
                end--;
            }
            arr[start] = arr[end];
            while(start < end && arr[start] <= tmp){
                start++;
            }
            arr[end] = arr[start];
        }
        arr[start] = tmp;
        return start;
    }

    /**
     *
     * @param arr
     *
     * 将一个数组递归成两个数组，
     * 一共四个指针，每两个指针维护一个数组，分别指向头尾，
     * 这两个头指针开始比较，将数字覆盖给原本的数组，
     */
    public static void MergeSort(int[] arr){
        MergeSort_Nor(arr);
        //MergeSort_1(arr,0,arr.length - 1);
    }
    /**
     * 归并排序递归实现
     * @param arr 数组
     * @param left 左边界
     * @param right 右边界
     */
    private static void MergeSort_1(int[] arr,int left, int right){
        if(left >= right){
            return;
        }
        int minIndex = (left + right) / 2;
        MergeSort_1(arr,left,minIndex);
        MergeSort_1(arr,minIndex + 1,right);

        MergeSort_2(arr,left,minIndex,right);


    }
    /**
     * 归并操作：合并两个有序区间 [left, mid] 和 [mid+1, right]
     * @param arr 数组
     * @param left 左边界
     * @param mid 中间分隔点
     * @param right 右边界
     */
    private static void  MergeSort_2(int[] arr,int left,int mid, int right){
        int point1 = left;
        int point2 = mid + 1;
        int index = 0;
        int[] array = new int[right - left + 1];

        while(point1 < mid + 1 && point2 <= right){
            if(arr[point1] <= arr[point2]){
                array[index] = arr[point1];
                index++;
                point1++;
            }else{
                array[index] = arr[point2];
                index++;
                point2++;
            }
        }
        while(point1 < mid + 1){
            array[index] = arr[point1];
            index++;
            point1++;
        }
        while(point2 <= right){
            array[index] = arr[point2];
            index++;
            point2++;
        }

        System.arraycopy(array, 0, arr, left, array.length);
    }
    /**
     * 归并排序非递归实现
     * 自底向上归并
     * @param arr 数组
     */
    private static void MergeSort_Nor(int[] arr){
        int gap = 1;
        while(gap < arr.length ){
            for (int i = 0; i < arr.length; i += gap*2) {
                int mid = i + gap - 1;
                if(mid >= arr.length){
                    mid = arr.length -1;
                }

                int R = i + 2 * gap -1;
                if(R >= arr.length){
                    R = arr.length -1;
                }
                MergeSort_2(arr,i,mid,R);
            }
            gap *= 2;
        }
    }

    /**
     * 计数排序
     * @param arr
     */
    public static void countingSort(int[] arr){
        if(arr == null){
            return;
        }

        int max = arr[0];
        int min = arr[0];
        for (int i = 0; i < arr.length; i++) {
            if(max < arr[i]){
                max = arr[i];
            }
            if(min > arr[i]){
                min = arr[i];
            }
        }
        int[] array = new int[max - min + 1];

        for (int i = 0; i < arr.length; i++) {
            int index = arr[i];
            array[index - min]++;
        }
        int k = 0;
        for (int i = 0; i < array.length;i++) {
            int count = array[i];
            while(count > 0){
                arr[k] = i+ min;
                count--;
                k++;
            }
        }
    }
}
