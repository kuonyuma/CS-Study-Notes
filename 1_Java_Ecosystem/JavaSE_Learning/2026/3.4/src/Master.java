import com.sun.source.tree.BreakTree;

import java.util.HashMap;

public class Master {

    public static void main(String[] args) {

    }
}
class Solution {

    public int[][] matrixBlockSum(int[][] mat, int k) {
        int lenrow = mat.length;
        int lencol = mat[0].length;
        int[][] sumarray = new int[lenrow + 1][lencol + 1];

        for (int i = 1; i <= lenrow; i++) {
            for (int j = 1; j <= lencol; j++) {
                sumarray[i][j] = sumarray[i][j - 1] + sumarray[i - 1][j] -
                        sumarray[i - 1][j - 1] + mat[i - 1][j - 1];
            }
        }

        int[][]sumarray2 = new int[lenrow][lencol];
        for (int i = 0; i < lenrow; i++) {
            for (int j = 0; j < lencol; j++) {
                int newrow = Math.min(lenrow,i + k + 1);
                int newcol = Math.min(lencol,j + k + 1);
                int oldrow = Math.max(1,i - k + 1);
                int oldcol = Math.max(1,j - k + 1);

                sumarray2[i][j] = sumarray[newrow][newcol] -
                        sumarray[oldrow - 1][newcol] - sumarray[newrow][oldcol - 1] +
                        sumarray[oldrow - 1][oldcol - 1];
            }
        }
        //扩展k个长度
        return sumarray2;
    }


    public int findMaxLength(int[] nums) {
        int[] array = java.util.Arrays.stream(nums)
                .map(n -> n == 0 ? -1 : n)
                .toArray();
        HashMap<Integer,Integer> hashMap = new HashMap<>();
        int sum = 0,maxlen = -1;
        hashMap.put(0,-1);
        for (int i = 0; i <nums.length ; i++) {
                sum  = sum + array[i];
                int buf = sum;
                if(hashMap.containsKey(buf)){
                    maxlen = Math.max(maxlen,(i - hashMap.get(sum) + 1));
                }
                if(!hashMap.containsKey(sum)){
                 hashMap.put(sum,i);
                }
        }
        return maxlen == -1 ? 0 :maxlen;
    }

}