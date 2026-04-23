class Solution {
    public int minSubArrayLen(int target, int[] nums) {
        int left = 0, right = 0;
        int sum = 0, len = Integer.MAX_VALUE;
        for(;right < nums.length ;right++){

            sum += nums[right];

            while(left <= right && sum >= target){
                if(len > right - left){
                    len = right - left + 1;
                }
                sum -= nums[left];
                left++;
            }
        }
        return len == Integer.MAX_VALUE ? 0 : len;
    }
}