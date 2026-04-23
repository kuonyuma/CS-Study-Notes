import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Solution {

    public List<Integer> findSubstring(String s, String[] words) {
        HashMap<String,Integer> hashmap_1 = new HashMap<>();
        List<Integer> list = new ArrayList<>();
        int len = words[0].length();
        for(String e:words) {
            hashmap_1.compute(e, (k, v) -> v == null ? 1 : v + 1);
        }
        int count = 0;
        HashMap<String,Integer> hashmap_2 = new HashMap<>();
        for(int i = 0;i < len ;i++){
            count = 0;
            hashmap_2.clear();
            for(int left = i,right = i;right +len <= s.length();right += len){
                String str = s.substring(right,right+len);
                hashmap_2.compute(str,(k,v)->v==null?1:v+1);
                if(hashmap_1.containsKey(str) && hashmap_2.get(str) <= hashmap_1.get(str)){
                    count++;
                }

                if((right - left) / len + 1 == words.length){
                    String out = s.substring(left,left+len);

                    if(count == words.length){
                        list.add(left);
                    }
                    if(hashmap_1.containsKey(out) && hashmap_2.get(out) <= hashmap_1.get(out)){
                        count--;
                    }
                    hashmap_2.compute(out, (k, v) ->v==null?-1: v - 1);
                    left += len;
                }
            }
        }
        return list;
    }

    public String minWindow(String s, String t) {
        if(t.length() > s.length())return "";

        int min_left= -1,min_right = -1;
        char[] hash_t = t.toCharArray();
        int[] map_t = new int[128];
        int need = 0;
        for (int i = 0; i < hash_t.length; i++) {
            char ch = hash_t[i];
            map_t[ch- 'A']++;
            need += map_t[ch - 'A'] == 1 ? 1:0;
        }
        char[] hash_s = s.toCharArray();
        int[] map_s = new int[128];
        for(int left =0 , right = 0 , count = 0,len = Integer.MAX_VALUE;right < s.length();right++){
            //进窗口
            char in = hash_s[right];
            map_s[in - 'A']++;

            if(map_s[in - 'A'] == map_t[in-'A']){
                count++;
            }
            while(count == need){

               if(len > right - left +1){
                   len = right - left +1;
                   min_left = left;
                   min_right = right;
               }

               char out = hash_s[left];

               if(map_s[out-'A']  == map_t[out-'A']){
                   count--;
               }
               map_s[out - 'A']--;
               left++;
            }
        }

        if(min_left == -1 && min_right == -1){
            return "";
        }
        return s.substring(min_left,min_right + 1);
    }

    public int peakIndexInMountainArray(int[] arr) {
        int left = 0, right = arr.length -1;
        while(left < right){
            int mid = left + (right - left) / 2;
            if(arr[mid] > arr[mid +1]) right = mid;
            else left = mid +1;
        }
        return left;
    }

    public int findPeakElement(int[] nums) {
        int left = 0, right = nums.length -1;

        while(left < right){
            int mid = left + (right - left) / 2;
            if(nums[mid] > nums[mid + 1]) right = mid;
            else left = mid + 1;
        }
        return left;
    }


    public int mySqrt(int xx) {
        int left = 0, right = Math.min(xx, 46341);

        while(left < right){
            int mid = left + (right - left + 1) / 2;
            long x =  (long)mid * mid;
            if(x > xx) right = mid - 1;
            else left = mid;
        }
        return left;
    }

    public int searchInsert(int[] nums, int target) {
        if(nums[0] > target) return 0;
        if(nums[nums.length -1] < target) return nums.length;
        int left = 0, right = nums.length - 1;

        while(left < right){
            int mid = left + (right - left) / 2;
            int x = nums[mid];
            if(x < target)left = mid +1;
            else right = mid;
        }
        return left;

    }

    public int[] searchRange(int[] nums, int target) {
        if(nums.length == 0) return new int[]{-1,-1};
        int left = 0, right = nums.length - 1;
        int[] arr ={-1,-1};
        while(left < right){//查找左端点
            int mid = left + (right - left) / 2;
            int x = nums[mid];
            if(x >= target){
                right = mid;
            }else{
                left = mid + 1;
            }
        }
        if(nums[left] == target){
            arr[0] = left;
        }

        left = 0;
        right = nums.length -1 ;

        while(left < right){//查找右端点
            int mid = left + (right - left + 1) / 2;
            int x = nums[mid];
            if(x > target){
                right = mid -1;
            }else{
                left = mid;
            }
        }
        if(nums[right] == target){
            arr[1] = right;
        }
        return arr;
    }
}
