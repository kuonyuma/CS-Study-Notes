import javax.swing.plaf.basic.BasicInternalFrameTitlePane;

public class Test {
    public static void main(String[] args) {
    Solution test = new Solution();
    int[] arr = {1,0,2,3,0,4,5,0};
    test.duplicateZeros(arr);
    for(int e: arr){
        System.out.print(e + " ");
    }

    }

}





class Solution {
    public int minSubArrayLen(int target, int[] nums) {

        int left  = 0 , right = 0;
        int sum = 0, len  = Integer.MAX_VALUE;
        for(;right < nums.length ; right++){
            sum += nums[right];//窗口扩大

            while(sum >= target){//判断
                len = Math.min(len,right - left + 1);//更新最小数值

                sum -= nums[left++];//窗口缩小
            }
        }

        return len == Integer.MAX_VALUE ? 0 : len;
    }
    public void moveZeroes(int[] nums) {
        int left = 0,right = 0;
        while(right < nums.length){
            if(nums[right] != 0){
                int tmp = nums[right];
                nums[right] = nums[left];
                nums[left] = tmp;
                left++;
            }
            right++;
        }
    }
    public void duplicateZeros(int[] arr) {
        int fast = -1, slow = 0;
        while(slow < arr.length){

            if(arr[slow] == 0){
                fast += 2;
            }else{
                fast++;
            }
            if(fast >= arr.length - 1){
                break;
            }
            slow++;
        }
        if(fast >= arr.length){
            slow--;
            fast--;
            arr[fast--] = 0;
        }

        while(fast > -1){
            if(arr[slow] == 0){
                arr[fast--] = 0;
                arr[fast--] = 0;
            }else{
                arr[fast--] = arr[slow];
            }
            slow--;
        }

    }
    public int func(int n){
        int ret = 0;
        while(n != 0){
            int buf = n % 10;
            ret += buf * buf;
            n /= 10;
        }
        return ret;
    }

    public boolean isHappy(int n) {
        int slow = n,fast = func(func(n));
        while(slow != fast){
            slow = func(slow);
            fast = func(func(fast));
        }
        if(slow == 1){
            return true;
        }else{
            return false;
        }
    }
    public int maxArea(int[] height) {
        int left = 0, right = height.length - 1;
        int max = -1;
        while(left < right){
            int len = right - left;
            int buf = len * Math.min(height[left],height[right]);
            if(max < buf){
                max = buf;
            }
            if(height[left] > height[right]){
                right--;
            }else{
                left++;
            }
        }
        return max;
    }
}