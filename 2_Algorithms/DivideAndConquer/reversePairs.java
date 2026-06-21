import java.util.ArrayList;
import java.util.List;


public class Test {
    int[] nums;
    int count;

    //返回逆序数对的总数
    public int reversePairs(int[] record) {
        //分治法
        count = 0;
        nums = new int[record.length];
        Sort(record, 0, record.length - 1);
        return count;

    }

    public void Sort(int[] arr, int l, int r) {

        if (l >= r) return;


        int mid = (r - l) / 2 + l;
        Sort(arr, l, mid);
        Sort(arr, mid + 1, r);


        int left = l;
        int right = mid + 1;
        int point = l;

        while (left <= mid && right <= r) {
            if (arr[left] <= arr[right]) {
                nums[point++] = arr[left++];
            } else {
                count += mid - left + 1;
                nums[point++] = arr[right++];
            }
        }
        while (right <= r)
            nums[point++] = arr[right++];
        while (left <= mid)
            nums[point++] = arr[left++];

        for (int i = l; i <= r; i++)
            arr[i] = nums[i];
    }
}
