
import java.util.ArrayList;
import java.util.List;


public class Test {
    int[] nums;
    int count;

    //返回逆序数对的总数
    public int reversePairs(int[] record) {
        //分治法
        count = 0;
        nums = new int[record.length];
        Sort(record,0,record.length - 1);
        return count;

    }

    public void Sort(int[] arr,int l,int r){

        if(l >= r)return;


        int mid = (r - l) / 2 + l;
        Sort(arr,l,mid);
        Sort(arr,mid + 1,r);


        int left = l;
        int right = mid + 1;
        int point = l;

        while(left <= mid && right <= r){
            if(arr[left] <= arr[right]){
                nums[point++] = arr[left++];
            }else{
                count += mid - left + 1;
                nums[point++] = arr[right++];
            }
        }
        while(right <= r)
            nums[point++] = arr[right++];
        while(left <= mid)
            nums[point++] = arr[left++];

        for(int i = l;i <= r;i++)
            arr[i] = nums[i];


    }

    int[] tmp;
    ArrayList<Integer> list = new ArrayList<>();

    public List<Integer> countSmaller(int[] nums) {
        tmp = new int[nums.length];
        megerSort(nums,0,nums.length - 1);
        return list;
    }
    public void megerSort(int[] nums,int l, int r){
        //分治
        if(l >= r) return;
        int mid = (r - l) / 2 + l;
        megerSort(nums,l,mid);
        megerSort(nums,mid + 1,r);

        //采用倒叙
        int left = l;
        int right = mid + 1;
        int point = l;
        while(left <= mid && right <= r){
            if(nums[left] <= nums[right]){
                tmp[point++] = nums[right++];
            }else{
                int count = r - right + 1;
                tmp[point] = nums[left];
                left++;
                int buf = list.get(point);
                list.add(point,count + buf);
                point++;
            }
        }
        while(left <= mid)
            tmp[point++] = nums[left++];
        while(right <= r)
            tmp[point++] = nums[right++];

        for(int i = l ;i <= r;i++){
            nums[i] = tmp[i];
        }
    }

}
