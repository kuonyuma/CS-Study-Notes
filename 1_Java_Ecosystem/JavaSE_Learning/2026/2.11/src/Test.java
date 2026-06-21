import java.util.*;

public class Test {
    public static void main(String[] args) {

    }
}
class Solution {
    public List<List<Integer>> threeSum(int[] nums) {
        Arrays.sort(nums);
        int i = 0;
        List<List<Integer>> list = new ArrayList<>();


        while(i < nums.length - 2){
            int a = nums[i];
            int left = i + 1;
            int right = nums.length - 1;
            while(left < right){
                int b = nums[left] + nums[right];
                if(b == -a){
                    List<Integer> list1 = new ArrayList<>();
                    list1.add(a);
                    list1.add(nums[left]);
                    list1.add(nums[right]);
                    list.add(list1);
                    while (left < right && nums[left] == nums[left + 1]) {
                        left++;
                    }
                    while (left < right && nums[right] == nums[right - 1]) {
                        right--;
                    }

                    left++;
                    right--;
                }else if(b > -a){
                    right--;
                }else{
                    left++;
                }
            }
            while(i < nums.length - 2){
                if(a != nums[++i]){
                 break;
                }
            }
            if(nums[i] > 0){
                return list;
            }
        }
        return list;
    }

    public List<List<Integer>> fourSum(int[] nums, int target) {
        Arrays.sort(nums);
        List<List<Integer>> list = new ArrayList<>();
        for (int i = 0; i < nums.length - 3; ) {
            int a = nums[i];
            for(int j = i + 1; j< nums.length- 2; ){
                int b = nums[j];

                int left  = j + 1;
                int right = nums.length - 1;

                while(left < right){
                    long sum =(long) nums[left] + nums[right];

                    if(sum + a + b == target){
                        List<Integer> list1 = new ArrayList<>();
                        list1.add(a);
                        list1.add(b);
                        list1.add(nums[left]);
                        list1.add(nums[right]);
                        list.add(list1);
                        while(left < right && nums[left] == nums[left + 1])left++;
                        while(left < right && nums[right] == nums[right - 1])right--;
                        left++;
                        right--;

                    }else if(sum > target - a - b){
                        right--;
                    }else{
                        left++;
                    }

                }
                while (j < nums.length - 2 && b == nums[j + 1])j++;
                j++;
            }
            while (i < nums.length - 3 && a == nums[i + 1])i++;
            i++;
        }
        return list;
    }


}