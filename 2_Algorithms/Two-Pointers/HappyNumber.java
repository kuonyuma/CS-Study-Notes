class Solution {

    public int func(int n){
        int ret = 0;
        while(n != 0){
            int buf = n % 10;
            ret += buf * buf;
            n /= 10;
        }
        return ret;
    }

    public boolean isHappy(int n) {
        int slow = n,fast = func(func(n));
        while(slow != fast){
            slow = func(slow);
            fast = func(func(fast));
        }
        if(slow == 1){
            return true;
        }else{
            return false;
        }
    }
}