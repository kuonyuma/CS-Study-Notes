package org.example;

import java.util.Arrays;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {


    }

    public int[] countBits(int n) {
        //方法2:
        {
            int[] arr = new int[n + 1];
            for (int i = 1; i <= n ; i++) {

                arr[i] = arr[i & (i - 1)] + 1;
            }
            return arr;
        }
    }
    public int hammingDistance(int x, int y) {
        //方法1：
//        {
//            int count = 0;
//            //将二进制位存入数组中
//            int[] arr1 = new int[31];
//            int[] arr2 = new int[31];
//            for (int i = 0; x != 0 ; i++) {
//                arr1[i] = x & 1;
//                x = x >> 1;
//            }
//            for (int i = 0; y != 0 ; i++) {
//                arr2[i] = y & 1;
//                y = y >> 1;
//            }
//            //两个指针遍历数组
//
//            for(int i = 0,j = 0;i < 31;i++,j++){
//                if(arr1[i] != arr2[j]){
//                    count++;
//                }
//            }
//            return count;
//        }
        /*
        利用异或运算符，可以把不同的二进位全部转换为1，因为这是无进位相加
        都是1则是0，都是0则是0，只有两个二进制位不一样时才会是1。
        此时1的数量就是二进位不同的数量
        再用KB算法可以快速求出1的个数
        */
        {
            int count = 0;
            int n  = x ^ y;
            while(n != 0){
                count  += n & 1;
                n  = n &(n - 1);
            }
            return count;
        }
    }
    public int singleNumber(int[] nums) {
        int buf = 0;
        for(int e : nums){
            buf ^= e;
        }
        return buf;
    }
    public int[] singleNumber_2(int[] nums) {
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
    public int missingNumber(int[] nums) {

        int buf = 0;
        for (int i = 0; i < nums.length; i++) {
            buf ^= nums[i];
        }
        for (int i = 0; i <= nums.length; i++) {
            buf ^= i;
        }
        return buf;
    }
    public int getSum(int a, int b) {
        int num1 = a ^ b;
        int num2 = (a & b) << 1;
        while(num2 != 0){
            int buf = (num1 & num2) << 1;//新的进位数
            num1 = num1 ^ num2;
            num2 = buf;
        }
        return num1;
    }
}