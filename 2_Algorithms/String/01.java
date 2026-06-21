class Solution {
    public String removeDuplicates(String s) {
        //创建一个数组模拟
        if(s == null) return null;
        int n = s.length();
        StringBuilder sb = new StringBuilder();

        for(int i = 0;i < n;i++){

            char ch = s.charAt(i);

            if(sb.length() >  0 && ch == sb.charAt(sb.length() - 1)){
                sb.deleteCharAt(sb.length() - 1);
            }else{
                sb.append(ch);
            }

        }

        return sb.toString();
    }
}