import java.util.concurrent.ThreadLocalRandom;

public class MySrot {
    //归并，类似于树的后续遍历
    int[]tmp;
    public void mergerSrot(int[] nums){
        int n = nums.length;
        tmp = new int[n];
        merger(nums,0, nums.length - 1);
    }
    private void merger(int[]nums,int l,int r){
        if(l >= r) return;
        int mid = (r - l) / 2 + l;

        merger(nums,l,mid);
        merger(nums,mid + 1,r);

        int left = l;
        int i = l;
        int right = mid + 1;

        while(left <= mid && right <= r){
            if(nums[left] >= nums[right]){//这里用降序
                tmp[i++] = nums[left++];
            }else{
                tmp[i++] = nums[right++];
            }
        }
        while(left <= mid)
            tmp[i++] = nums[left++];

        while(right <= r)
            tmp[i++] = nums[right++];

        for(int j = l;j <= r;j++){
            nums[j] = tmp[j];
        }

    }
    //快速，类似于Tree的前序遍历
    public void mergerSort_2(int[] nums){
        if(nums == null || nums.length < 2){
            return;
        }
        merger_2(nums,0, nums.length - 1);

    }
    private void merger_2(int[] nums,int l,int r){
        int index = ThreadLocalRandom.current().nextInt(l,r);
        int judge = nums[index];

        int left = l - 1;
        int right = r + 1;
        int point = l;
        while(point < right){
            if(nums[point] < judge){
                swap(nums,left+1,point);
                left++;
                point++;
            }else if(nums[point] > judge){
                swap(nums,right - 1,point);
                right--;

            }else{
                point++;
            }
        }

        merger_2(nums,l,left);
        merger_2(nums,right,r);
    }
    private void swap(int[] nums,int index1,int index2){
        int tmp = nums[index2];
        nums[index2] = nums[index1];
        nums[index1] = tmp;
    }

}
