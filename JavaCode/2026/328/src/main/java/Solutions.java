import java.util.HashMap;

public class Solutions {

    public int[] twoSum(int[] nums, int target) {
        //key为真实的数值，val为下标
        HashMap<Integer,Integer> hashMap = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int num = target - nums[i];
                //如果没有该数值
                if(!hashMap.containsKey(num)) {
                    hashMap.put(nums[i], i);
                }else{
                    return new int[]{i,hashMap.get(num)};
                }
        }
        return new int[]{1,1};
    }

    public boolean CheckPermutation(String s1, String s2) {
        if(s1.length() != s2.length())
            return false;
        // A == 65 Z == 90  a == 97 z == 122
        int[] hash = new int[128];
        char[] chs1 = s1.toCharArray();
        for(char ch : chs1)
            hash[ch]++;

        char[] chs2 = s2.toCharArray();
        for(char ch : chs2)
            hash[ch]--;

        for(int e: hash)
            if(e != 0)
                return false;

        return true;
    }

    public int[] twoSum_2(int[] nums,int target){
        HashMap<Integer,Integer> map = new HashMap<>();
        for(int i = 0;i < nums.length;i++){
            int x = target - nums[i];
            if(map.containsKey(x)){
                return new int[]{i,map.get(x)};
            }else{
                map.put(nums[i],i);
            }
        }
        return new int[]{-1,-1};
    }
}
