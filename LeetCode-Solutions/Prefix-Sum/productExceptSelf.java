class Solution {
    public int[] productExceptSelf(int[] nums) {
        int len = nums.length;
        int[] sum1 = new int[len + 1];
        int[] sum2 = new int[len + 1];
        sum1[0] = sum2[len - 1] = 1;

        for (int i =  1; i < len; i++) {
            sum1[i] = sum1[i - 1] * nums[i-1];
        }

        for (int i = len - 2; i >= 0; i--) {
            sum2[i] = sum2[i + 1] * nums[i + 1];
        }

        int[] array = new int[len];
        for (int i = 0; i < len; i++) {
            array[i] = sum1[i] * sum2[i];
        }
        return array;
    }
}