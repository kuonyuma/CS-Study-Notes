class Solution {
    public int minOperations(int[] nums, int x) {
        int sum_1 = 0;
        for(int e:nums){
            sum_1 += e;
        }
        if(sum_1 - x < 0){
            return -1;
        }
        int left = 0, right = 0,max_len = -1,sum = 0;

        while(right < nums.length){
            sum += nums[right];

            while(sum > sum_1 - x){
                sum -= nums[left++];
            }

            if(sum == sum_1 - x){
                max_len = Math.max(max_len,right - left + 1);
            }

            right++;
        }
        return max_len == -1 ? -1 : nums.length - max_len;
    }
}