class Solution {
    public int takeAttendance(int[] records) {
        int buf = 0;
        for(int i = 0;i < records.length;i++){
            buf ^= records[i];
        }
        for(int i = 0; i < records.length + 1;i++ ){
            buf ^= i;
        }
        return buf;
    }
}

