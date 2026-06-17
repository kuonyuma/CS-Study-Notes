import java.util.Queue;

class Solution {
    public boolean canFinish(int n, int[][] p) {
        // 准备容器
        HashMap<Integer, List<Integer>> map = new HashMap<>();
        int[] inDegree = new int[n];

        // 建表
        for (int i = 0; i < p.length; i++) {
            int a = p[i][0];
            int b = p[i][1];
            // b->a
            if (!map.containsKey(b)) {
                map.put(b, new ArrayList<>());
            }
            map.get(b).add(a);
            inDegree[a]++;
        }
        // 拓扑排序
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0)
                queue.add(i);
        }

        while (!queue.isEmpty()) {
            int a = queue.poll();
            List<Integer> tmp = map.remove(a);
            if (tmp != null) {

                for (int e : tmp) {
                    inDegree[e]--;
                    if (inDegree[e] == 0)
                        queue.add(e);
                }
            }
        }
        // 判断是否回环
        for (int i = 0; i < n; i++) {
            if (inDegree[i] != 0)
                return false;
        }
        return true;
    }
}