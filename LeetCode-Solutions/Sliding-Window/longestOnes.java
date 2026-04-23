class Solution {
    public int longestOnes(int[] nums, int k) {
        int left = 0, right = 0, max_len = 0, count = 0;
        while(right < nums.length){
            if(nums[right] == 0){
                count++;
            }
            while(count > k){
                if(nums[left] == 0) {
                    count--;
                }
                left++;
            }
            max_len = Math.max(max_len,right - left + 1);
            right++;
        }
        return max_len;
    }
}