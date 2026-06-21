import java.util.LinkedList;
import java.util.Queue;

class Solution {
    // 标记数组
    boolean[][] mark;
    int R, C;

    int[] dr = { 1, -1, 0, 0 };
    int[] dc = { 0, 0, 1, -1 };

    public int numEnclaves(int[][] grid) {

        R = grid.length;
        C = grid[0].length;

        mark = new boolean[R][C];
        Queue<int[]> queue = new LinkedList<>();
        // 遍历第一行
        for (int j = 0, i = 0; j < C; j++) {
            if (grid[i][j] == 1) {
                queue.add(new int[] { i, j });
                mrak[i][j] = true;
            }

        }

        // 遍历最后一行
        for (int j = 0, i = R - 1; j < C; j++) {
            if (grid[i][j] == 1) {
                mark[i][j] = true;
                queue.add(new int[] { i, j });
            }
        }
        // 遍历第一列
        for (int i = 0, j = 0; i < R; i++) {
            if (grid[i][j] == 1) {
                queue.add(new int[] { i, j });
                mark[i][j] = true;
            }
        }

        // 遍历最后一列
        for (int i = 0, j = C - 1; i < R; i++) {
            if (grid[i][j] == 1) {
                mark[i][j] = true;
                queue.add(new int[] { i, j });
            }
        }

        while (!queue.isEmpty()) {
            int[] tmp = queue.poll();
            int r = tmp[0];
            int c = tmp[1];

            for (int i = 0; i < 4; i++) {
                int newr = r + dr[i];
                int newc = c + dc[i];
                if (newr >= 0 && newc >= 0 && newr < R && newc < C &&
                        !mark[newr][newc] && grid[newr][newc] == 1) {
                    queue.add(new int[] { newr, newc });
                    mark[newr][newc] = true;
                }
            }
        }
        int result = 0;
        for (i = 0; i < grid.length; i++)
            for (int j = 0; j < C; j++) {
                if (!mark[i][j] && grid[i][j] == 1)
                    result++;
            }
        return result;
    }
}