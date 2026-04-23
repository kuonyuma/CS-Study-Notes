import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

private static int[] sort_2(int[] nums, int k, int l, int r){

    int randomIndex = ThreadLocalRandom.current().nextInt(l, r + 1);
    int pivot = nums[randomIndex];

    int left = l - 1;
    int right = r + 1;
    int point = l;

    while(right > point){
        if(nums[point] < pivot){
            swap(nums,left + 1,point);
            point++;
            left++;
        }
        else if(nums[point] > pivot){
            swap(nums,right - 1,point);
            right--;
        }else{
            point++;
        }
    }

    //算元素个数
    if(k <= left - l + 1){
        return sort_2(nums,k,l,left);
    }else if(k > right - 1 - l + 1){
        return sort_2(nums,k - (right - l),right,r);
    }else{
        return Arrays.copyOfRange(nums,l,l + k);
    }
}
public static int[] inventoryManagement(int[] stock, int cnt) {
    return sort_2 (stock,cnt,0,stock.length - 1);
}