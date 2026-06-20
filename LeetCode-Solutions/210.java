import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collector;
import java.util.stream.Collectors;

class Solution {
    public int[] findOrder(int numCourses, int[][] prerequisites) {
        // 一个数组记录入度
        int[] inDegree = new int[numCourses];
        Arrays.stream(prerequisites).forEach(p -> inDegree[p[0]]++);

        // 用哈希表构建一个关系图谱
        Map<Integer, List<Integer>> map = Arrays
                .stream(prerequisites)
                .collect(Collectors.groupingBy(p -> p[1],
                        Collectors.mapping(p -> p[0], Collectors.toList())));

        // 将入度为0的元素添加至队列
        Queue<Integer> que = new LinkedList<>();
        IntStream.range(0, numCourses).filter(i -> inDegree[i] == 0)
                .forEach(que::add);

        // 准备一个接收容器
        List<Integer> result = new ArrayList<>();
        while (!que.isEmpty()) {
            // 弹出入度为0的元素
            int tmp = que.poll();
            result.add(tmp);
            List<Integer> tmpValue = map.remove(tmp);
            if (tmpValue != null) {
                for (int i = 0; i < tmpValue.size(); i++) {
                    inDegree[tmpValue.get(i)]--;
                    if (inDegree[tmpValue.get(i)] == 0)
                        que.add(tmpValue.get(i));
                }
            }

        }
        // 检查回环
        if (IntStream.range(0, numCourses).anyMatch(i -> inDegree[i] != 0))
            return new int[0];

        int[] array = result.stream().mapToInt(Integer::intValue).toArray();
        return array;
    }
}