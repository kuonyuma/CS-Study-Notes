class Solution {
    public int pivotIndex(int[] nums) {
        //使用一个下标，比如f[i] = nums[i - 1]+nums[i - 2]+...nums[0];
        //再定义一个g[i] =
        // nums[nums.length - 1] + nums[nums.length - 2]+..nums[i + 1];
        int len = nums.length;
        int[] f = new int[len + 1];
        for(int i = 1; i <= len;i++){
            f[i] = f[i - 1] + nums[i - 1];
        }
        int[] g = new int[len +1];
        for(int i = len -2; i >= 0;i--){
            g[i] = g[i + 1] + nums[i + 1];
        }
        for (int i = 0; i < nums.length; i++) {
            if(g[i] == f[i])
                return i;
        }
        return -1;

    }
}