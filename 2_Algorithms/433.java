import java.util.Map;
import java.util.Queue;

class Solution {
    Map<String, Boolean> bankMap;
    char[] base;
    int count = 0;

    public int minMutation(String startGene, String endGene, String[] bank) {

        // 将bank中的基因存入hash表中
        bankMap = new HashMap<>();
        for (String e : bank) {
            bankMap.put(e, false);
        }
        base = new char[] { 'A', 'C', 'G', 'T' };

        if (!bankMap.containsKey(endGene)) {
            return -1;
        }

        if (startGene.equals(endGene)) {
            return 0;
        }

        // 队列实现宽度搜索
        Queue<String> bfs = new LinkedList<>();

        bfs.add(startGene);

        while (!bfs.isEmpty()) {
            int size = bfs.size();

            // 层数
            for (int k = 0; k < size; k++) {
                String tmpString = bfs.poll();
                bankMap.put(tmpString, true);
                for (int i = 0; i < tmpString.length(); i++) {

                    for (int j = 0; j < 4; j++) {
                        char[] chars = tmpString.toCharArray();
                        char ch = base[j];
                        chars[i] = ch;

                        String result = new String(chars);

                        if (bankMap.containsKey(result) && !bankMap.get(result)) {

                            if (result.equals(endGene)) {
                                return count + 1;
                            }

                            bankMap.put(result, true);
                            bfs.add(result);
                        }
                    }
                }
            }
            count++;
        }
        return -1;
    }
}