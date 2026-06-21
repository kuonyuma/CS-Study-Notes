class Solution {
    // 距离
    int count = 0;

    int[] dr = { 1, -1, 0, 0 };
    int[] dc = { 0, 0, 1, -1 };

    // 标记数组
    boolean[][] mark;
    int rlen;
    int clen;

    public int nearestExit(char[][] maze, int[] entrance) {
        rlen = maze.length;
        clen = maze[0].length;

        mark = new boolean[rlen][clen];

        Queue<int[]> queue = new LinkedList<>();
        queue.add(entrance);
        mark[entrance[0]][entrance[1]] = true;

        while (!queue.isEmpty()) {
            int size = queue.size();

            for (int i = 0; i < size; i++) {
                int[] tmp = queue.poll();
                int r = tmp[0];
                int c = tmp[1];

                if ((r != entrance[0] || c != entrance[1]) &&
                        (r == 0 || r == rlen - 1 || c == 0 || c == clen - 1)) {
                    return count;
                }

                for (int j = 0; j < 4; j++) {
                    int newr = r + dr[j];
                    int newc = c + dc[j];

                    if (newc >= 0 && newc < clen && newr >= 0 && newr < rlen && !mark[newr][newc] &&
                            maze[newr][newc] == '.') {

                        mark[newr][newc] = true;
                        queue.add(new int[] { newr, newc });
                    }
                }
            }
            count++;
        }
        return -1;
    }
}