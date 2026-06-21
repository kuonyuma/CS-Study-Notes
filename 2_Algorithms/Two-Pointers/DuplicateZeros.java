class Solution {
    public void duplicateZeros(int[] arr) {
        int fast = -1, slow = 0;
        while(slow < arr.length){
            if(arr[slow] == 0){
                fast += 2;
            }else{
                fast++;
            }
            if(fast >= arr.length - 1){
                break;
            }
            slow++;
        }

        if(fast >= arr.length){
            slow--;
            fast--;
            arr[fast--] = 0;

        }

        while(fast > -1){
            if(arr[slow] == 0){
                arr[fast--] = 0;
                arr[fast--] = 0;
            }else{
                arr[fast--] = arr[slow];
            }
            slow--;
        }
    }
}