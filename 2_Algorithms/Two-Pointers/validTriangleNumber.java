class Solution {
    public int triangleNumber(int[] nums) {
        Arrays.sort(nums);
        int left = 0,right = nums.length - 2;
        int count = 0;
        for(int i = nums.length -1;i >= 2;i--){
            int c = nums[i];
            left = 0;
            right = i - 1;
            while(left < right){
                int a = nums[left];
                int b = nums[right];
                if(a+b > c){
                    count += right - left;
                    right--;
                    left = 0;
                }else{
                    left++;

                }
            }
        }
        return count;
    }
}