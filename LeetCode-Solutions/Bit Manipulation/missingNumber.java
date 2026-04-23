class Solution {
    public int missingNumber(int[] nums) {

        int buf = 0;
        for (int i = 0; i < nums.length; i++) {
            buf ^= nums[i];
        }
        for (int i = 0; i <= nums.length; i++) {
            buf ^= i;
        }
        return buf;
    }
}