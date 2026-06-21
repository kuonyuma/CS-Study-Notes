class Solution {
    public int[] searchRange(int[] nums, int target) {
        if(nums.length == 0) return new int[]{-1,-1};
        int left = 0, right = nums.length - 1;
        int[] arr ={-1,-1};
        while(left < right){//查找左端点
            int mid = left + (right - left) / 2;
            int x = nums[mid];
            if(x >= target){
                right = mid;
            }else{
                left = mid + 1;
            }
        }
        if(nums[left] == target){
            arr[0] = left;
        }

        left = 0;
        right = nums.length -1 ;

        while(left < right){//查找右端点
            int mid = left + (right - left + 1) / 2;
            int x = nums[mid];
            if(x > target){
                right = mid -1;
            }else{
                left = mid;
            }
        }
        if(nums[right] == target){
            arr[1] = right;
        }
        return arr;
    }
}