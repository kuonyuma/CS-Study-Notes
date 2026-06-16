import java.util.Queue;

class Solution {
    int[] dr = { 1, -1, 0, 0 };
    int[] dc = { 0, 0, 1, -1 };

    int R, C;

    public int[][] updateMatrix(int[][] mat) {

        // 队列实现bfs
        Queue<int[]> queue = new LinkedList<>();

        R = mat.length;
        C = mat[0].length;

        // 创建一个数组
        int[][] result = new int[R][C];

        for (int i = 0; i < R; i++)
            for (int j = 0; j < C; j++) {
                if (mat[i][j] == 0) {
                    result[i][j] = 0;
                    queue.add(new int[] { i, j });
                    continue;
                }
                result[i][j] = -1;
            }
        while (!queue.isEmpty()) {
            int[] tmp = queue.poll();
            int r = tmp[0], c = tmp[1];

            for (int i = 0; i < 4; i++) {
                int newr = r + dr[i];
                int newc = c + dc[i];

                if (newr >= 0 && newr < R && newc >= 0 && newc < C &&
                        result[newr][newc] == -1) {
                    result[newr][newc] = result[r][c] + 1;
                    queue.add(new int[] { newr, newc });
                }
            }
        }
        return result;
    }
}