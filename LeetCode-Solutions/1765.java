import java.util.Queue;

class Solution {
    int[] dr = { 1, -1, 0, 0 };
    int[] dc = { 0, 0, 1, -1 };

    public int[][] highestPeak(int[][] isWater) {

        int ROW = isWater.length;
        int COL = isWater[0].length;
        int[][] result = new int[ROW][COL];
        Queue<int[]> queue = new LinkedList<>();

        for (int i = 0; i < ROW; i++)
            for (int j = 0; j < COL; j++) {
                if (isWater[i][j] == 1) {
                    result[i][j] = 0;
                    queue.add(new int[] { i, j });
                    continue;
                }
                result[i][j] = -1;

            }
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
        return result;
    }
}