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
        }
        return list;
    }
}