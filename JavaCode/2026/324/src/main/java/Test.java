import java.util.ArrayList;
import java.util.List;

public class Test {
}

class Solution2 {
    int[] tmpnums;
    int[] index;
    int[] tmpindex;
    int[] arr;
    //求有多少个数比当前的数字小
    public List<Integer> countSmaller(int[] nums){
        int len = nums.length;
        tmpnums = new int[len];
        index = new int[len];
        tmpindex = new int[len];
        arr = new int[len];

        for(int i = 0; i < len;i++){
            index[i] = i;
        }
        mergerSort(nums,0,len - 1);
        ArrayList<Integer> list = new ArrayList<Integer>();
        for(int i = 0;i < len;i++){
            list.add(arr[i]);
        }

        return list;
    }
    public void mergerSort(int[] nums,int l ,int r){

        if(l >= r) return;
        int mid = (r - l) / 2 + l;
        mergerSort(nums,l,mid);
        mergerSort(nums,mid + 1,r);

        merger(nums,l,r,mid);
    }
    public void merger(int[] nums,int l ,int r, int mid){
        int left = l;
        int right = mid + 1;
        int i = l;
        while(left <= mid && right <= r){
            if(nums[left] > nums[right]){
                arr[index[left]] +=(r - right + 1);

                tmpindex[i] = index[left];
                tmpnums[i++] = nums[left++];
            }else{
                tmpindex[i] = index[right];
                tmpnums[i++] = nums[right++];
            }
        }
        while(left <= mid){
            tmpindex[i] = index[left];
            tmpnums[i++] = nums[left++];
        }
        while(right <= r){
            tmpindex[i] = index[right];
            tmpnums[i++] = nums[right++];
        }

        for(int point = l;point <= r;point++){
            nums[point] = tmpnums[point];
            index[point] = tmpindex[point];
        }
    }
}
