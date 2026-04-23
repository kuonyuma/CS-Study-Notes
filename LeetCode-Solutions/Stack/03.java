
class Solution {
    public String decodeString(String s) {
        // stack_str 存储的是在遇到 [ 之前的字符串前缀
        Stack<StringBuilder> stack_str = new Stack<>();
        // stack_int 存储的是 [ 之前的倍数
        Stack<Integer> stack_int = new Stack<>();

        StringBuilder currentStr = new StringBuilder();
        int num = 0;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (Character.isDigit(c)) {
                // 计算数字（处理多位数的情况）
                num = num * 10 + (c - '0');
            } else if (c == '[') {
                // 遇到左括号，将当前的倍数和已经积累的字符串存入栈
                stack_int.push(num);
                stack_str.push(currentStr);
                // 重置，用于收集 [] 内部的新字符串
                num = 0;
                currentStr = new StringBuilder();
            } else if (c == ']') {
                // 遇到右括号，开始出栈
                int times = stack_int.pop();
                StringBuilder prevStr = stack_str.pop();

                // 将当前 currentStr 重复 times 次，并追加到 prevStr 后面
                for (int j = 0; j < times; j++) {
                    prevStr.append(currentStr);
                }
                // 更新 currentStr 为拼接后的结果，方便后续可能的嵌套处理
                currentStr = prevStr;
            } else {
                // 普通字母，追加到当前正在收集的字符串
                currentStr.append(c);
            }
        }
        return currentStr.toString();
    }
}