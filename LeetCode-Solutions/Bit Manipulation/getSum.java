class Solution {
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