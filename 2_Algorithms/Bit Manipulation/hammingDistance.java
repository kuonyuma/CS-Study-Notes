class Solution {
    public int hammingDistance(int x, int y) {
         /*{
            int count = 0;
            //将二进制位存入数组中
            int[] arr1 = new int[31];
            int[] arr2 = new int[31];
            for (int i = 0; x != 0 ; i++) {
                arr1[i] = x & 1;
                x = x >> 1;
            }
            for (int i = 0; y != 0 ; i++) {
                arr2[i] = y & 1;
                y = y >> 1;
            }
            //两个指针遍历数组

            for(int i = 0,j = 0;i < 31;i++,j++){
                if(arr1[i] != arr2[j]){
                    count++;
                }
            }
            return count;
        }
        */
        {

            int count = 0;
            int n  = x ^ y;
            while(n != 0){
                count++;
                n  = n &(n - 1);
            }
            return count;
        }
    }
}