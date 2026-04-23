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
}