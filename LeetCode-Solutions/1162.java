import java.util.LinkedList;
import java.util.Queue;

class Solution {
    int[] dr = { 1, -1, 0, 0 };
    int[] dc = { 0, 0, -1, 1 };

    public int maxDistance(int[][] grid) {

        int ROW = grid.length;
        int COL = grid[0].length;
        int[][] result = new int[ROW][COL];
        Queue<int[]> queue = new LinkedList<>();

        for (int i = 0; i < ROW; i++)
            for (int j = 0; j < COL; j++) {
                if (grid[i][j] == 1) {
                    queue.add(new int[] { i, j });
                    result[i][j] = 0;
                    continue;
                }
                result[i][j] = -1;
            }

        int landCount = queue.size();
        if (landCount == 0 || landCount == ROW * COL)
            return -1;

        while (!queue.isEmpty()) {
            int[] tmp = queue.poll();
            int r = tmp[0];
            int c = tmp[1];

            for (int i = 0; i < 4; i++) {
                int newr = r + dr[i];
                int newc = c + dc[i];
                if (newc >= 0 && newr >= 0 && newr < ROW && newc < COL &&
                        result[newr][newc] == -1) {
                    result[newr][newc] = result[r][c] + 1;
                    queue.add(new int[] { newr, newc });
                }
            }
        }
        int count = 0;
        for (int i = 0; i < ROW; i++)
            for (int j = 0; j < COL; j++) {
                count = Math.max(count, result[i][j]);
            }
        return count;
    }
}