class Solution {
    public int searchInsert(int[] nums, int target) {
        if(nums[0] > target) return 0;
        if(nums[nums.length -1] < target) return nums.length;
        int left = 0, right = nums.length - 1;

        while(left < right){
            int mid = left + (right - left) / 2;
            int x = nums[mid];
            if(x < target)left = mid +1;
            else right = mid;
        }
        return left;

    }
}