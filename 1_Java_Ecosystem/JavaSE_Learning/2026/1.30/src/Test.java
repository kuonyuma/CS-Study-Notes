public class Test {
    /**
     * 双指针法，移动0元素
     * @param nums
     */
    public static void moveZeroes(int[] nums) {
        int dest = 0;
        int cur = 0;
        while(cur < nums.length){
            if(nums[cur] == 0){
                cur++;
            }else{
                int tmp = nums[cur];
                nums[cur] = nums[dest];
                nums[dest] = tmp;
                dest++;
                cur++;
            }
        }
    }

    /**
     * 双指针法，复写0元素
     * @param arr
     */
    public static void duplicateZeros(int[] arr) {
        int dest = -1;
        int cur = 0;
        while(cur < arr.length){
            if(arr[cur] == 0) {
                dest +=2;
            }else{
                dest++;
            }
            if(dest >= arr.length-1){
                break;
            }
            cur++;
        }

        if(dest == arr.length){
            arr[--dest] = 0;
            dest--;
            cur--;
        }

        //此时cur已经指向目标元素
        while(dest >= 0 ){
            if(arr[cur] != 0){
                arr[dest] = arr[cur];
                dest--;
                cur--;
            }else{
                arr[dest] = 0;
                arr[dest - 1] = 0;
                dest -= 2;
                cur--;
            }
        }
    }

    public static void main1(String[] args) {
        int[] arr = {1,0,2,3,0,4,5,0};
        duplicateZeros(arr);
        for(int e : arr){
            System.out.print(e + " ");
        }
    }
    //复习
    public static void main(String[] args) {
        //func1( new int[]{1,0,2,3,0,4,5,0});
        func2( new int[]{1,0});
    }
    public static void func2(int[] arr){
        int dest = -1;
        int cur = 0;
        while(dest < arr.length){
            if(arr[cur] == 0){
                dest+=2;
            }else{
                dest++;
            }
            if(dest >= arr.length -1){
                break;
            }
            cur++;
        }
        if(dest > arr.length - 1){
            arr[--dest] = 0;
            dest--;
            cur--;
        }
        //问题
        while(cur >= 0){
            if(arr[cur] != 0){
                arr[dest] = arr[cur];
                dest--;
            }else{
                arr[dest--] = 0;
                arr[dest--] = 0;
            }
            cur--;
        }
        for(int e: arr){
            System.out.print(e + " ");
        }
    }


    public static void func1(int[] arr){
        int dest = -1;
        int cur = 0;
        //分区
        while(cur < arr.length){
            if(arr[cur] != 0){
                int tmp = arr[cur];
                arr[cur] = arr[++dest];
                arr[dest] = tmp;
            }
            cur++;
        }
        for(int e: arr){
            System.out.print(e + " ");
        }
    }



}


