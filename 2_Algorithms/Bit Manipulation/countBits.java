class Solution {
    public int[] countBits(int n) {
        //方法1，
        /*{
            int[] arr = new int[n + 1];
            for(int i = 0;i < n + 1;i++ ){
                int count = 0;
                int buf = i;
                while(buf != 0){
                    count += 1;
                    buf = buf & (buf - 1);
                }
                arr[i] = count;
            }
            return arr;
        }
*/
        //方法2.
        {
            int[] ans = new int[n + 1];
            for(int i = 1;i<=n;i++){
                ans[i] = ans[i & (i - 1)] + 1;
            }
            return ans;
        }
    }
}