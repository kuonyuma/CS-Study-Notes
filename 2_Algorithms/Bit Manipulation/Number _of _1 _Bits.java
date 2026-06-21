class Solution {
    /*public int hammingWeight(int n) {
        int count = 0,buf;
        while(n != 0){
            count += (n & 1);
            n = (n >> 1);
        }
     return count;
    }*/

    //方法二：
    //n  &= (n - 1);可以将处于最右端的二进制 1抵消掉，
    //看能抵消多少次就有多少个二进制1。
    public int hammingWeight(int n) {
        int count = 0;
        while(n != 0){
            count += 1;
            n  &= (n - 1);
        }
        return count;
    }
}