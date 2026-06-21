class Solution {
    public int findPoisonedDuration(int[] timeSeries, int duration) {
        int count = 0;
        for(int i = 0,j = i + 1;j < timeSeries.length;i++,j++){
            int left = timeSeries[i];
            int right = timeSeries[j];
            if(right - left < duration){
                count += right - left;
            }else{
                count += duration;
            }
        }
        count += duration;
        return count;
    }
}