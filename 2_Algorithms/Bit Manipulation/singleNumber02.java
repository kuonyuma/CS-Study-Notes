class Solution {
    public int singleNumber(int[] nums) {
        int ret = 0;
        for (int i = 0; i < 32; i++) {

            int sum = 0;
            for(int e : nums){
                sum += (e >> i) & 1;
            }
            sum %= 3;
            if(sum == 1){
                ret |= sum << i;
            }
        }
        return ret;
    }
}