class Solution {
    public int lengthOfLongestSubstring(String s) {
        char[] chars = s.toCharArray();
        int left = 0,right = 0;
        int len = -Integer.MAX_VALUE;
        int[] array = new int[128];

        while(right < s.length()){
            array[chars[right]]++;
            while(array[chars[right]] > 1){
                array[chars[left++]]--;
            }
            len = Math.max(len,right - left + 1);
            right++;
        }
        return len == -Integer.MAX_VALUE ? 0 :len;
    }
}