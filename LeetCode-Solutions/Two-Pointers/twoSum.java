class Solution {
    public int[] twoSum(int[] price, int target) {
        int left = 0, right = price.length -1;
        while(left < right){
            int sum = price[left] + price[right];
            if(sum == target){
                return new int[]{price[left],price[right]};
            }
            if(sum < target){
                left++;
            }else{
                right--;
            }
        }
        return new int[0];
    }
}