class Solution {
    public int calculate(String s) {
        Stack<Integer> stack = new Stack<>();
        char op = '+';
        int num = 0;
        //便利字符串
        for(int i = 0;i < s.length();i++){
            //提取数字
            char tmp = s.charAt(i);
            // isDigit()判断字符是否是0-9；
            if(Character.isDigit(tmp)){
                num = num * 10 + (tmp - '0');
            }
            if(!Character.isDigit(tmp) && tmp !=' '
                    || i == s.length() - 1){
                if(op == '+'){
                    stack.add(num);
                }else if(op == '-'){
                    stack.add(-num);
                }else if(op == '*'){
                    stack.add(stack.pop() * num);
                }else{
                    stack.add(stack.pop() / num);
                }

                op = tmp;
                num = 0;
            }


        }
        //便利stack
        int sum = 0;
        while(!stack.isEmpty()){
            sum += stack.pop();
        }
        return sum;
    }
}