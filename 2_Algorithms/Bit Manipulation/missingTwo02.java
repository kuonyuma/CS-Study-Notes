class Solution {
    public int[] missingTwo(int[] nums) {
        int len = nums.length + 2;
        int sum = 0;
        for(int e : nums){
            sum ^= e;
        }
        for(int i = 1;i <= len; i++){
            sum ^=i;
        }
        int num2 = sum & (-sum);
        int[] mark = new int[2];
        for(int e : nums){
            if((e & num2) == 0){
                mark[0] ^= e;
            }else{
                mark[1] ^= e;
            }
        }
        for(int e =1; e <= len ;e++){
            if((e & num2) == 0){
                mark[0] ^= e;
            }else{
                mark[1] ^= e;
            }
        }
        return mark;

    }
}