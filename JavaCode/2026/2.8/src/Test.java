import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Solution {

    /**
     * 计算一个整数各位数字的平方和
     */
    public int getSquareSum(int num){
        int sum = 0;
        while(num != 0){
            int buf = num % 10;
            sum += buf * buf;
            num /= 10;
        }
        return sum;
    }

    /**
     * 判断一个数是否为快乐数
     * 使用快慢指针思想（Floyd判圈算法）
     */
    public boolean isHappy(int n) {
        int slow = n,fast = n;
        while(true){
            slow = getSquareSum(slow);
            fast = getSquareSum(getSquareSum(fast));

            if(slow == fast){
                break;
            }
        }
        return slow == 1;
    }

    /**
     * 盛最多水的容器
     * 使用对撞双指针，每次移动高度较小的一侧
     */
    public int maxArea(int[] height) {
        int left = 0,right = height.length - 1;
        List<Integer> list = new ArrayList<>();
        while(left != right){
            if(height[left] <= height[right]){
                list.add((right - left)*Math.min(height[left],height[right]));
                left++;
            }else{
                list.add((right - left)*Math.min(height[left],height[right]));
                right--;
            }
        }
        int max = list.remove(0);
        while(!list.isEmpty()){
            int buf = list.remove(0);
            if(max <= buf){
                max = buf;
            }
        }
        return max;
    }

    /**
     * 有效三角形的个数
     * 排序 + 双指针。先固定最长边，然后在左侧区间使用对撞指针寻找满足 a+b > c 的组合。
     */
    public int triangleNumber(int[] nums) {
        Arrays.sort(nums);

        int left = 0,right = nums.length - 2;
        int count = 0;
        for(int i = nums.length -1;i >= 2;i--){
            int c = nums[i];
            right = i - 1;
            left = 0;
            while(left < right){
                int a = nums[left];
                int b = nums[right];
                if(a+b > c){
                    // 如果 a+b > c，由于数组已排序，left 到 right 之间的所有 a 都能与当前的 b 组成三角形
                    count += right - left;
                    right--;
                }else{
                    left++;
                }
            }
        }
       return count;
    }

    /**
     * 和为 s 的两个数字
     * 在递增数组中使用对撞双指针
     */
    public int[] twoSum(int[] price, int target) {
        int left = 0, right = price.length -1;
        while(left < right){
            int sum = price[left] + price[right];
            if(sum == target){
                return new int[]{price[left],price[right]};
            }
            if(sum < target){
                left++;
            }else{
                right--;
            }
        }
        return new int[0];
    }

}

public class Test {
    public static void main(String[] args) {

    }
}