class Solution {
    public int[] singleNumber(int[] nums) {
        int buf = 0;
        for(int e : nums){
            buf ^= e;
        }
        int mun2 = buf & (-buf);
        int[] arr = new int[2];
        for(int e : nums){
            if((e & mun2) == 0){
                arr[0] ^= e;
            }else{
                arr[1] ^= e;
            }
        }
        return arr;
    }
}