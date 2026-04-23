class Solution {
    public boolean isUnique(String astr) {
        int map = 0;
        if(astr.length() > 26){
            return false;
        }
        for (int i = 0; i < astr.length(); i++) {
            char ch = astr.charAt(i);
            int x = ch - 'a';
            if(((map >> x) & 1) == 1){
                return false;
            }else{
                map |= 1 << x;
            }
        }
        return true;
    }
}