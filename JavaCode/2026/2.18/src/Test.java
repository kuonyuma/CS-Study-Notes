import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {
    public static void main(String[] args) {
    Solution sol = new Solution();

    List<Integer> list = sol.findAnagrams("cbaebabacd","abc");
    for(int e:list){
        System.out.print(e+" ");
    }
    }
}
class Solution {
    public int totalFruit(int[] fruits) {
        int left = 0, right = 0, count = 0,len = 0;
        Map<Integer,Integer> hashmap = new HashMap<>();
        for( ; right < fruits.length ; right++){
            if(!hashmap.containsKey(fruits[right])){
                count++;
            }
            hashmap.compute(fruits[right],(k,v) -> v == null ? 1:v+1);
            while(count > 2){
                hashmap.put(fruits[left],hashmap.get(fruits[left]) -1);
                if(hashmap.get(fruits[left]) == 0){
                    hashmap.remove(fruits[left]);
                    count--;
                }
                left++;
            }
            len = Math.max(len,right - left +1);
        }
        return len;
    }

    public List<Integer> findSubstring(String s, String[] words) {
        HashMap<String, Integer> hashMap_p = new HashMap<>();
        for(String str : words){
            hashMap_p.compute(str,(k,v)->v == null ? 1:v+1);
        }

        List<Integer> list = new ArrayList<>();
        HashMap<String, Integer> hashMap_s = new HashMap<>();
        int len = words[0].length();
        for(int i = 0;i< len ;i++){
            int count = 0;
            hashMap_s.clear();

            for(int right = i,left = i;right + len <= s.length();right+= len){
                //进窗口
                String str = s.substring(right,right+len);
                hashMap_s.compute(str,(k,v)->v == null ? 1:v+1);
                if(hashMap_p.get(str) != null &&hashMap_s.get(str) <= hashMap_p.get(str)){
                    count += len;
                }
                if((right - left)/len + 1 == words.length){
                    //出窗口
                    if(count == words.length * len){
                        list.add(left);
                    }
                    String str_1 = s.substring(left,left+len);
                    if(hashMap_p.get(str_1)!= null && hashMap_s.get(str_1) <= hashMap_p.get(str_1)){
                        count -= len;
                    }
                    hashMap_s.compute(str_1,(k,v)-> v-1);
                    left += len;
                }
            }
        }
        return list;
    }

    public List<Integer> findAnagrams(String s, String p) {
        HashMap<Character,Integer> hashmap_1 = new HashMap<>();
        HashMap<Character,Integer> hashmap_2 = new HashMap<>();
        List<Integer> list = new ArrayList<>();
        int left = 0,right = 0;

        for(int i = 0;i < p.length();i++){
            hashmap_1.compute(p.charAt(i),(k,y) -> y == null ? 1: y+1);
        }

        while(right < s.length()){
            hashmap_2.compute(s.charAt(right),(k,y) -> y == null ? 1: y+1);

            if(right - left + 1 == p.length()){
                if (hashmap_1.equals(hashmap_2)) {
                    list.add(left);
                }
                hashmap_2.compute(s.charAt(left),(x,y) -> y-1);
                if(hashmap_2.get(s.charAt(left)) == 0){
                    hashmap_2.remove(s.charAt(left));
                }
                left++;
            }
            right++;
        }
        return list;
    }

    public List<Integer> findAnagrams_1(String s, String p) {
        char[] ss = s.toCharArray();
        char[] pp = p.toCharArray();
        int[] hash_p = new int[26];
        int[] hash_s = new int[26];
        List<Integer> list = new ArrayList<>();

        for(char e : pp){
            hash_p[e - 'a']++;
        }

        for(int left = 0, right = 0, count = 0;right < s.length();right++){
            char ch_s = ss[right];
            hash_s[ch_s - 'a']++;//进窗口

            if(hash_s[ch_s - 'a'] <= hash_p[ch_s - 'a']){
                count++;//count标记有效字符
            }

            if(right - left + 1 == p.length()){
                if(count == p.length()){
                    list.add(left);
                }

                char ch = ss[left];
                if(hash_s[ch - 'a'] <= hash_p[ch - 'a']){
                    count--;
                }
                hash_s[ch - 'a']--;
                left++;
            }


        }
        return list;
    }
    public List<Integer> findAnagrams_2(String s, String p) {
        List<Integer> list = new ArrayList<>();
        int[] hash_p = new int[26];
        for(int i  = 0 ; i< p.length() ; i++){
            char ch = s.charAt(i);
            hash_p[ch -'a']++;
        }
        int[] hash_s = new int[26];
        for( int left = 0 , right = 0,count =0;right < s.length();right++){
            char ch2 = s.charAt(right);
            hash_s[ch2 - 'a']++;//进窗口
            if(hash_s[ch2 - 'a'] <= hash_p[ch2 -'a'])count++;

            if(right - left + 1 == p.length()){

                if(count == p.length()){
                    list.add(left);
                }

                //出窗口
                char out  = s.charAt(left);
                if(hash_s[out - 'a'] < hash_p[out - 'a']){
                    count--;
                }
                hash_s[out -'a']--;
                left++;
            }
        }
        return list;
    }

}
