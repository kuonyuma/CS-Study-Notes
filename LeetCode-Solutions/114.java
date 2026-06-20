import java.util.*;
import java.util.stream.Collectors;

class Solution {
    public String alienOrder(String[] words) {

        // 1. 入度数组（保持 int[] 不变，这个本来就是对的）
        int[] inDegree = new int[26];

        // 2. 收集所有出现过的字符（新增）
        Set<Character> chars = new HashSet<>();
        for (String w : words)
            for (char c : w.toCharArray())
                chars.add(c);

        // 3. 邻接表用 Set 去重（原来是 Map<Char, List>，改成 Set）
        Map<Character, Set<Character>> map = new HashMap<>();

        for (int i = 0; i < words.length - 1; i++) {
            String w1 = words[i];
            String w2 = words[i + 1];

            // 4. 前缀检查（放在内层循环之前，你已经改对了）
            if (w1.length() > w2.length() && w1.startsWith(w2))
                return "";

            for (int j = 0; j < Math.min(w1.length(), w2.length()); j++) {
                char c1 = w1.charAt(j);
                char c2 = w2.charAt(j);

                if (c1 != c2) {
                    // 5. 用 computeIfAbsent + Set.add 同时建边和去重
                    Set<Character> neighbors = map.computeIfAbsent(c1, k -> new HashSet<>());
                    if (neighbors.add(c2)) // 新边才增加入度
                        inDegree[c2 - 'a']++;
                    break;
                }
            }
        }

        // 6. 只将出现过的、入度为0的字符入队（不是全部26个）
        Queue<Character> queue = new LinkedList<>();
        for (char c : chars)
            if (inDegree[c - 'a'] == 0)
                queue.add(c);

        // 7. BFS 拓扑排序
        StringBuilder result = new StringBuilder();
        while (!queue.isEmpty()) {
            char tmp = queue.poll();
            result.append(tmp);
            Set<Character> neighbors = map.get(tmp);
            if (neighbors != null) {
                for (char next : neighbors) {
                    if (--inDegree[next - 'a'] == 0)
                        queue.add(next);
                }
            }
        }

        // 8. 结果长度 != 出现字符数 → 有环
        return result.length() == chars.size() ? result.toString() : "";
    }
}