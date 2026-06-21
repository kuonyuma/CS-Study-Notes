public class Solution{
    int count;
    int[] tmp;

    public int reversePairs(int[] nums){
        int len = nums.length;
        count = 0;
        tmp = new int[len];
        mergerSort(nums, 0,len - 1);
        return count;
    }
    public void mergerSort(int[]nums,int l ,int r){
        if(l >= r) return;
        int mid = (r - l) / 2 + l;
        mergerSort(nums,l,mid);
        mergerSort(nums,mid + 1,r);

        merger(nums,l,r,mid);
    }
    public void merger(int[] nums,int l,int r,int mid){
        int left = l;
        int right = mid + 1;
        int point = l;


        int cur1 = l;
        int cur2 = mid + 1;

        //逆序数组
        while(cur1 <= mid && cur2 <= r){
            if((long)nums[cur1] > 2L * nums[cur2]){
                count += r - cur2 + 1;
                cur1++;
            }else{
                cur2++;
            }
        }

        while(left <= mid && right <= r){
            if(nums[left] >= nums[right]){
                tmp[point] = nums[left];
                point++;
                left++;

            }else{
                tmp[point++] = nums[right++];
            }
        }

        while(left <= mid){
            tmp[point++] = nums[left++];
        }
        while(right <= r)
            tmp[point++] = nums[right++];

        for(int i = l ;i <= r;i++){
            nums[i] = tmp[i];
        }
    }


}
