class Solution {
    public int maxArea(int[] height) {
        int left = 0, right = height.length - 1;
        int max = -1;
        while(left < right){
            int len = right - left;
            int buf = len * Math.min(height[left],height[right]);
            if(max < buf){
                max = buf;
            }
            if(height[left] > height[right]){
                right--;
            }else{
                left++;
            }
        }
        return max;
    }
}