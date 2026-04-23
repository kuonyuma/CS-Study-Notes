package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.printf("Hello and welcome!");

        for (int i = 1; i <= 5; i++) {
            //TIP Press <shortcut actionId="Debug"/> to start debugging your code. We have set one <icon src="AllIcons.Debugger.Db_set_breakpoint"/> breakpoint
            // for you, but you can always add more by pressing <shortcut actionId="ToggleLineBreakpoint"/>.
            System.out.println("i = " + i);
        }
    }

}

class Solution {
    public int singleNumber(int[] nums) {
        int ret = 0;
        for (int i = 0; i < 32; i++) {

            int sum = 0;
            for(int e : nums){
                sum += (e >> i) & 1;
            }
            sum %= 3;
            if(sum == 1){
                ret |= sum << i;
            }
        }
        return ret;
    }

    /**
     * 给定一个数组，包含从 1 到 N 所有的整数，但其中缺了两个数字。你能在 O(N) 时间内只用 O(1) 的空间找到它们吗？
     *
     * 以任意顺序返回这两个数字均可。
     *
     * 示例 1：
     *
     * 输入：[1]
     * 输出：[2,3]
     * 示例 2：
     *
     * 输入：[2,3]
     * 输出：[1,4]
     * 提示：
     *
     * nums.length <= 30000
     * @param nums
     * @return
     */
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