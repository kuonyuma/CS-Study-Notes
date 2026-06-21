import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.concurrent.ThreadLocalRandom;

public class Solution {

    private Solution(){}

    private static int sort(int[] nums,int k,int l,int r){

        int randomIndex = ThreadLocalRandom.current().nextInt(l, r + 1);
        int pivot = nums[randomIndex];

            int left = l - 1;
            int right = r + 1;
            int point = l;

            while(right > point){
                if(nums[point] < pivot){
                    swap(nums,left + 1,point);
                    point++;
                    left++;
                }
                else if(nums[point] > pivot){
                    swap(nums,right - 1,point);
                    right--;
                }else{
                    point++;
                }
            }

            //算元素个数
            if(k <= r - right + 1){
                return sort(nums,k,right,r);
            }else if(k > r - left ){
                return sort(nums,k - (r - left),l,left);
            }else{
                return nums[right - 1];
            }
        }


        public static int findKthLargest(int[] nums, int k){
            return sort(nums,k,0,nums.length - 1);
        }

        public static int haepSelect(int[] nums, int k){
            PriorityQueue<Integer> heap = new PriorityQueue<>();
            for(int e : nums)
                heap.offer(e);//默认小排在上面
            while(heap.size() > k)
                heap.poll();
            return heap.poll();
        }
        public static int heapSelect_2(int[] nums,int k){
            if(k < 1 || k > nums.length){
                throw new IllegalArgumentException("k must be between 1 and " + nums.length + ", but was " + k
                );
            }

            PriorityQueue<Integer> heap = new PriorityQueue<>();
            for(int e : nums){
                if(heap.size() < k){
                    heap.offer(e);
                }else{
                    int tmp = heap.peek();
                    if(tmp < e){
                        heap.poll();
                        heap.offer(e);
                    }
                }
            }
            return heap.peek();
        }
    private static void swap(int[] nums,int index_1, int index_p){
        int tmp = nums[index_1];
        nums[index_1] = nums[index_p];
        nums[index_p] = tmp;
    }

    private static int[] sort_2(int[] nums,int k,int l,int r){

        int randomIndex = ThreadLocalRandom.current().nextInt(l, r + 1);
        int pivot = nums[randomIndex];

        int left = l - 1;
        int right = r + 1;
        int point = l;

        while(right > point){
            if(nums[point] < pivot){
                swap(nums,left + 1,point);
                point++;
                left++;
            }
            else if(nums[point] > pivot){
                swap(nums,right - 1,point);
                right--;
            }else{
                point++;
            }
        }

        //算元素个数
        if(k <= left - l + 1){
            return sort_2(nums,k,l,left);
        }else if(k > right - 1 - l + 1){
            return sort_2(nums,k - (right - l),right,r);
        }else{
            return Arrays.copyOfRange(nums,l,l + k);
        }
    }
    public static int[] inventoryManagement(int[] stock, int cnt) {
        return sort_2 (stock,cnt,0,stock.length - 1);
    }
}



