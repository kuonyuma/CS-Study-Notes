class Solution {
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
}