
class Solution {
    // 向量数组
    int[] dr = { 0, 0, -1, 1 };
    int[] dc = { 1, -1, 0, 0 };
    // 矩阵的长宽
    int forestRow;
    int forestCol;
    // 标记数组统计走过的单元格，以免重复
    boolean[][] mark;

    public int cutOffTree(List<List<Integer>> forest) {
        forestRow = forest.size();
        forestCol = forest.get(0).size();
        // 获取目标
        List<int[]> targets = new ArrayList<>();
        for (int i = 0; i < forestRow; i++) {
            for (int j = 0; j < forestCol; j++) {
                if (forest.get(i).get(j) > 1) {
                    targets.add(new int[] { i, j });
                }
            }
        }
        // 目标数组排序
        Collections.sort(targets,
                (a, b) -> forest.get(a[0]).get(a[1]) - forest.get(b[0]).get(b[1]));
        // 假设现在需要写出到达目标点位的最小步骤，
        int result = 0;
        int beginR = 0;
        int beginC = 0;
        for (int[] target : targets) {

            int endR = target[0];
            int endC = target[1];

            int count = bfs(forest, beginR, beginC, endR, endC);
            if (count == -1)
                return -1;
            result += count;

            beginC = endC;
            beginR = endR;
        }
        return result;
    }

    private int bfs(List<List<Integer>> f, int beginR, int beginC, int endR, int endC) {

        if (beginC == endC && beginR == endR)
            return 0;
        // 定义队列
        Queue<int[]> que = new LinkedList<>();
        que.add(new int[] { beginR, beginC });
        mark = new boolean[forestRow][forestCol];
        mark[beginR][beginC] = true;
        int count = 0;
        // 标记数组

        while (!que.isEmpty()) {
            int plies = que.size();
            for (int i = 0; i < plies; i++) {
                int[] tmp = que.poll();
                int r = tmp[0];
                int c = tmp[1];

                for (int k = 0; k < 4; k++) {
                    int newR = r + dr[k];
                    int newC = c + dc[k];

                    if (newR >= 0 && newR < forestRow && newC >= 0 && newC < forestCol &&
                            !mark[newR][newC] && f.get(newR).get(newC) != 0) {
                        if (newR == endR && newC == endC)
                            return count + 1;
                        mark[newR][newC] = true;
                        que.add(new int[] { newR, newC });
                    }
                }
            }
            count++;
        }
        return -1;
    }
}