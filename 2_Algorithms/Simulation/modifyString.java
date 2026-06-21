class Solution {
    public String modifyString(String s) {

        char[] chars = s.toCharArray();
        int len = chars.length;

        for(int i = 0;i < len;i++){
            char ch = chars[i];
            if(ch == '?'){

                for(char ch1 = 'a'; ch1 <= 'z';ch1++){
                    boolean left = false;
                    boolean right = false;
                    if(i == 0 || chars[i - 1] != ch1){
                        left = true;
                    }
                    if(i == len - 1 || chars[i + 1] != ch1){
                        right = true;
                    }

                    if(left && right){
                        chars[i] = ch1;
                    }
                }
            }
        }
        return String.valueOf(chars);
    }
}