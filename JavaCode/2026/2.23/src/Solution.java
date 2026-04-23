import java.util.Scanner;

public class Solution {



    //右端数值为基准
    public int findMin(int[] nums) {
        int base = nums[nums.length - 1];
        int left = 0, right = nums.length - 1;
        while(left < right){
            int mid = left + (right - left) / 2;
            if(nums[mid] < base) right = mid;
            else left = mid + 1;
        }
        return nums[left];
    }
    //左端数值为基准
    public int findMin2(int[] nums) {
        if (nums[0] <= nums[nums.length - 1]) return nums[0];
        int base = nums[0];
        int left = 0, right = nums.length - 1;
        while(left < right){
            int mid = left + (right - left) / 2;
            if(nums[mid] < base) right = mid;
            else left = mid + 1;
        }
        return left == nums.length -1?base: nums[left];
    }

    public int takeAttendance(int[] records) {
        int left = 0, right = records.length - 1;
        int mid = right;
        if(records[mid] == mid)return mid + 1;
        while(left < right){
            mid = left + (right - left ) / 2;
            if(records[mid] == mid) left = mid + 1;
            else right = mid;
        }
        return left;
    }
    public int takeAttendance_tryHash(int[] records) {
        int[] hash = new int[records.length + 1];
        int buf = -1;
        for(int e : records){
            hash[e]++;
        }
        for(int i = 0; i < hash.length;i++){
            if(hash[i] == 0){
                buf = i;
            }
        }
        return buf;
    }
    public int takeAttendance_try(int[] records) {
        int buf = 0;
        for(int i = 0;i < records.length;i++){
            buf ^= records[i];
        }
        for(int i = 0; i < records.length + 1;i++ ){
            buf ^= i;
        }
        return buf;
    }

    public void Test(){
        Scanner input = new Scanner(System.in);
        //输入处理
        int n = input.nextInt(),q = input.nextInt();
        int[]arr = new int[n+1];
        for(int i = 1; i <= n;i++)arr[i] = input.nextInt();
        //预处理
        long[] prefixSumArray = new long[n + 1];
        for (int i = 1; i <= n; i++) {
            prefixSumArray[i] = arr[i] + prefixSumArray[i - 1];
        }
        //使用数据
        while(q > 0){
            int l = input.nextInt();
            int r = input.nextInt();
            System.out.println(prefixSumArray[r] - prefixSumArray[l -1]);
            q--;
        }
    }

}
