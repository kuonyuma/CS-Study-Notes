
import java.util.HashSet;

public class Test {
    public static void main(String[] args) {
    Solution test = new Solution();
    int[] arr = {1,1,4,2,3};
    int a = test.minOperations(arr,5);
        System.out.println(a);
    }
}
class Solution {
    public int lengthOfLongestSubstring(String s) {
       int left = 0 , right = 0,len = 0;
       char[] chars = s.toCharArray();
       int[] array = new int[128];
       while(right < s.length()){
           array[chars[right]]++;
           while(array[chars[right]] > 1){
               array[chars[left++]]--;
           }
           len = Math.max(len,right - left + 1);
           right++;
       }
       return len;
    }
    public int lengthOfLongestSubstring_2(String s) {
        int left = 0 , right = 0,len = 0;
        HashSet<Character> set = new HashSet<>();
        while(right < s.length()){
            char ch = s.charAt(right);
            while(set.contains(ch)){
                set.remove(s.charAt(left++));
            }
            set.add(ch);
            len = Math.max(len, set.size());
            right++;
        }
        return len;
    }

    public int longestOnes(int[] nums, int k) {
        int left = 0, right = 0, max_len = 0, count = 0;
        if(nums[right++] == 0){
            count++;
        }
        while(right < nums.length){
            while(count >= k){
                if(nums[left++] == 0) {
                    count--;
                }
            }
            max_len = Math.max(max_len,right - left + 1);
            right++;
        }
        return max_len;
    }

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