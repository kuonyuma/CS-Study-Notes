class Solution {
    public double myPow(double x, int n) {
        if (n == 0)
            return 1.0;
        double tmp;
        if (n > 0) {
            tmp = 1 * myPow(x, n / 2) * 1.0;
        } else {
            return 1 / (x * myPow(x, -(n + 1)) * 1.0);
        }

        if (n % 2 == 0) {
            return tmp * tmp * 1.0;
        } else {
            return tmp * tmp * 1.0 * x;
        }
    }
}