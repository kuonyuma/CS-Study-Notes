import java.util.concurrent.ThreadLocalRandom;

public class Test {

    public static void main(String[] args) {
        Solution solution = new Solution();
        int num = solution.findKthLargest(new int[]{3,2,3,1,2,4,5,5,6},4);
        System.out.println(num);
    }
}

class Solution {

    public int sort(int[] nums,int k,int l,int r){

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
        if(k <= r - right + 1){
            return sort(nums,k,right,r);
        }else if(k > r - left ){
            return sort(nums,k - (r - left),l,left);
        }else{
            return nums[right - 1];
        }
    }

    public void swap(int[] nums,int index_1, int index_p){
        int tmp = nums[index_1];
        nums[index_1] = nums[index_p];
        nums[index_p] = tmp;
    }

    public int findKthLargest(int[] nums, int k){
        return sort(nums,k,0,nums.length - 1);
    }
}
