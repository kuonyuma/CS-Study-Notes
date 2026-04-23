class Solution {
    public int mySqrt(int xx) {
        int left = 0,  right = Math.min(xx, 46341);

        while(left < right){
            int mid = left + (right - left + 1) / 2;
            long x =  (long)mid * mid;
            if(x > xx) right = mid - 1;
            else left = mid;
        }
        return left;
    }
}