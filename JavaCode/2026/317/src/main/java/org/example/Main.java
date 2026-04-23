package org.example;
import java.util.Scanner;

public class Main {
            public static void main(String[] args) {
                Scanner in = new Scanner(System.in);
                //数据处理
                int n, m, q;
                n = in.nextInt();
                m = in.nextInt();
                q = in.nextInt();
                int[][] arr = new int[n][m];
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < m; j++) {
                        arr[i][j] = in.nextInt();
                    }
                }
                //构建前缀和
                int[][] sum = new int[n + 1][m + 1];
                for (int i = 1; i <= n; i++) {
                    for (int j = 1; j <= m; j++) {
                        sum[i][j] = sum[i][j - 1] + sum[i - 1][j] - sum[i - 1][j - 1] + arr[i - 1][j - 1];
                    }
                }
                //使用前缀和
                while (q > 0) {
                    int oldrow = in.nextInt();
                    int oldcol = in.nextInt();
                    int row = in.nextInt();
                    int col = in.nextInt();

                    int ret;
                    ret = sum[row][col] - sum[row][oldcol - 1] -
                            sum[oldrow - 1][col] + sum[oldrow - 1][oldcol - 1];
                    System.out.println(ret);
                    q--;
                }
            }

    /*
给你一个整数数组 nums ，请计算数组的 中心下标 。

数组 中心下标 是数组的一个下标，其左侧所有元素相加的和等于右侧所有元素相加的和。

如果中心下标位于数组最左端，那么左侧数之和视为 0 ，因为在下标的左侧不存在元素。这一点对于中心下标位于数组最右端同样适用。

如果数组有多个中心下标，应该返回 最靠近左边 的那一个。如果数组不存在中心下标，返回 -1 。
*/
    public int pivotIndex(int[] nums) {
        int len = nums.length;
        int[] sum = new int[len + 1];
        //预处理
        for(int i = 1; i <= len ;i++){
            sum[i] = sum[i - 1] + nums[i - 1];
        }

        for(int i = 1;i <= len ; i++){
            int left = sum[i - 1];
            int right =sum[len] - sum[i];
            if(left == right){
                return i - 1;
            }
        }
        return -1;
    }



}