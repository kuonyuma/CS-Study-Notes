package lc;

import java.util.Stack;

class Solution {
    public String decodeString(String _s) {
        //双栈
        Stack<StringBuilder> stack_str = new Stack<>();
        stack_str.push(new StringBuilder());
        Stack<Integer> stack_int = new Stack<>();

        int i = 0;
        while(i < _s.length()){
            //处理数字
            int num = 0;
            if(Character.isDigit(_s.charAt(i))){

                while(Character.isDigit(_s.charAt(i))){
                    num = num * 10 + (_s.charAt(i) - '0');
                    ++i;
                }
                //将数字push
                stack_int.push(num);
            }
            //当tmp == '['
            else if(_s.charAt(i) == '['){
                stack_str.push(new StringBuilder());
                ++i;
            } else if (_s.charAt(i) == ']') {
                int stack_pop_num = stack_int.pop();
                StringBuilder stack_pop_str = stack_str.pop();
                StringBuilder tmp = stack_pop_str;
                while(stack_pop_num-- > 1)
                    stack_pop_str.append(tmp);

                stack_str.peek().append(stack_pop_str);
            }

            else{
                stack_str.peek().append(_s.charAt(i));
                ++i;
            }
        }
        return stack_str.peek().toString();
    }
}