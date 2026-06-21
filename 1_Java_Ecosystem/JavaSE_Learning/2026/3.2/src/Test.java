import java.lang.invoke.DelegatingMethodHandle$Holder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println();

        Solution solution = new Solution();
        int[] arr = solution.productExceptSelf(new int[]{1,2,3,4});
    }
}
class Solution {

    public int pivotIndex(int[] nums) {
        //一般的前缀和定义：f[3]表示第原数组从第一个元素一直加到第3个元素
        /*
         for(int i = 1; i <=   len;i++){
            f[i] = f[i - 1] + nums[i - 1];
        }
         */
        int len = nums.length;
        int[] f = new int[len + 1];
        for(int i = 1; i <= len;i++){
            f[i] = f[i - 1] + nums[i - 1];//这里不能这样写，与下面的后缀和不对称
        }
        //由suffixSum的定义来定义后缀和，不过此时的后缀和要稍微变换一下
        //sum[i]表示的是第i+1个元素的后缀和
        int[] g = new int[len + 1];
        for(int i = len - 1; i >= 0;i--){
            g[i] = g[i + 1] + nums[i];
        }
        for (int i = 0; i < nums.length; i++) {
            if(g[i] == f[i])
                return i;
        }
        return -1;
    }
    public void suffixSum(int[] nums){
        int[] sum = new int[nums.length + 1];
        //后缀和的定义：sum[1]表示原数组的第一个元素一直加到最后一个元素
        //这样就可以从下标直观看出是原数组的第几个数的后缀和。减少了下标与实际元素个数的换算
        for (int i = nums.length - 1; i >= 0; i--) {
            sum[i] = sum[i + 1] + nums[i];
        }
        for(int e : sum){
            System.out.print(e + " ");
        }
    }
    public int[] productExceptSelf(int[] nums) {
        int len = nums.length;
        int[] sum1 = new int[len + 1];
        int[] sum2 = new int[len + 1];
        sum1[0] = sum2[len - 1] = 1;

        for (int i =  1; i < len; i++) {
            sum1[i] = sum1[i - 1] * nums[i-1];
        }

        for (int i = len - 2; i >= 0; i--) {
            sum2[i] = sum2[i + 1] * nums[i + 1];
        }

        int[] array = new int[len];
        for (int i = 0; i < len; i++) {
         array[i] = sum1[i] * sum2[i];
        }
        return array;
    }

    public int subarraySum(int[] nums, int k) {
        HashMap<Integer,Integer> map = new HashMap<>();
        int sum = 0;
        int count = 0;
        map.put(0,1);
        for (int i = 0; i < nums.length; i++) {
            sum = sum + nums[i];
            int buf = sum - k;
            if(map.containsKey(buf)){
                count += map.get(buf);
            }
            map.compute(sum,(x,y)->y == null ? 1 : y+1);
        }
        return count;
    }

    public int subarraysDivByK(int[] nums, int k) {
        /*
        两个数如果余数相同，那么详=相减之后的余数等于0
         */
        HashMap<Integer,Integer> map = new HashMap<>();
        int sum = 0, count = 0;
        map.put(0,1);
        for (int i = 0; i < nums.length; i++) {
            sum = sum + nums[i];
            int mod = (sum % k + k) % k;//将余数始终保持在正数


            if(map.containsKey(mod)){
                count += map.get(mod);
            }
            map.compute(mod,(x,y)-> y == null ? 1 : y + 1);
        }
        return count;
    }
}