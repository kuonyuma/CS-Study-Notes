import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

class Solution {
    public int ladderLength(String beginWord, String endWord,
            List<String> wordList) {

        // 准备好26个字母数组
        char[] base = new char[26];
        for (int i = 0; i < 26; i++) {
            base[i] = (char) ('a' + i);
        }
        // 将单词放入哈希表中
        Map<String, Boolean> map = new HashMap<>();
        for (String e : wordList) {
            map.put(e, false);
        }
        // 统计最小路径
        int count = 1;

        // 队列实现宽度搜索
        Queue<String> queue = new LinkedList<>();
        map.put(beginWord, true);
        queue.add(beginWord);
        while (!queue.isEmpty()) {

            // 一层一层的遍历
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                char[] tmp = queue.poll().toCharArray();

                // 单词的长度
                for (int j = 0; j < tmp.length; j++) {
                    // 一个字符变化26次
                    for (int k = 0; k < 26; k++) {
                        char[] tmp2 = Arrays.copyOf(tmp, tmp.length);
                        tmp2[j] = base[k];
                        String s = new String(tmp2);

                        if (map.containsKey(s) && !map.get(s)) {

                            if (s.equals(endWord)) {
                                return count + 1;
                            }
                            map.put(s, true);
                            queue.add(s);
                        }
                    }
                }
            }
            count++;
        }
        return 0;

    }
}