package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.printf("Hello and welcome!");

        for (int i = 1; i <= 5; i++) {
            //TIP Press <shortcut actionId="Debug"/> to start debugging your code. We have set one <icon src="AllIcons.Debugger.Db_set_breakpoint"/> breakpoint
            // for you, but you can always add more by pressing <shortcut actionId="ToggleLineBreakpoint"/>.
            System.out.println("i = " + i);
        }
    }


        public String countAndSay(int n) {
            String ret = "1";
            for (int i = n - 1; i > 0; i--) {//几轮操作
                StringBuilder tmp = new StringBuilder();
                for (int left = 0, right = 0; right < ret.length(); ) {
                    while (right < ret.length() &&
                            ret.charAt(left) == ret.charAt(right)) right++;
                    int count = right - left;

                    tmp.append(Integer.toString(count));
                    tmp.append(ret.charAt(left));

                    left = right;
                }
                ret = tmp.toString();
            }
            return ret;
        }

    public String convert(String s, int numRows) {
        int len = s.length();
        char[] array = new char[len];
        int T = 2 * numRows - 2;
        int i = 0;
        //处理特殊情况
        if (numRows <= 1 || len <= numRows) {
            return s;
        }

        //处理第一行
        for(int point = 0;point < len;point += T){
            array[i] = s.charAt(point);
            i++;
        }

        //处理中间行
        for(int r = 1;r < numRows -1;r++){

            for(int point = r;point < len;point += T){
                array[i] = s.charAt(point);
                i++;
                int diagonal = point + T - 2 * r; // 关键公式
                if (diagonal < len) { // 必须检查是否越界
                    array[i++] = s.charAt(diagonal);
                }
            }
        }


        //处理尾行
        for(int point = numRows - 1;point < len;point += T){
            array[i] = s.charAt(point);
            i++;
        }
        return String.valueOf(array);
    }

    public String convert2(String s, int numRows) {
        // 第k行： k + t,t - k + t
        int T = numRows + numRows -2;//周期
        StringBuilder tmp = new StringBuilder();
        if(s.length() <= 1|| s.length() <= numRows){
            return s;
        }

        //处理第一行
        for(int i = 0;i < s.length();i += T){
            tmp.append(s.charAt(i));
        }

        //处理中间行数
        for(int i = 1;i < numRows - 1;i++){//处理有多少行
            for(int k = i;k < s.length();k += T){//处理具体的中间某一行

                tmp.append(s.charAt(k));
                int diagonal = k + T - 2 * i; // 斜列字符
                if (diagonal < s.length()) {
                    tmp.append(s.charAt(diagonal));
                }
            }

        }

        //处理最后一行
        for(int i = numRows -1;i < s.length();i += T){
            tmp.append(s.charAt(i));
        }

        return tmp.toString();

    }
}