import java.time.chrono.IsoChronology;
import java.util.Queue;
import java.util.Stack;

public class Test {
    public static void main(String[] args) {

    }
    public static void main1(String[] args) {
        int[] arr1= {1,2,3,4,5};
        int[] arr2 = {4,5,3,2,1};
        System.out.println(IsPopOrder(arr1,arr2));


    }

     public static boolean IsPopOrder (int[] pushV, int[] popV) {
        Stack<Integer> stack = new Stack<>();
        int j = 0;
        for (int i = 0; i < pushV.length; i++) {
           stack.push(pushV[i]);

           while(j < popV.length&& !stack.empty()&&stack.peek() == popV[j]){
               stack.pop();
               j++;
           }
        }
      return stack.empty();
    }
    public static int evalRPN(String[] tokens) {
        //后缀表达式求值
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < tokens.length; i++) {
            String ch =  tokens[i];
            if(ch.equals("+") ||ch.equals("-") || ch.equals("*") || ch.equals("/")){
                int val1 = stack.pop();
                int val2 = stack.pop();
                int result = switch(ch){
                    case "+"->val2 + val1;
                    case "-"->val2 - val1;
                    case "*"->val2 * val1;
                    case "/"->val2 / val1;
                    default -> -1;
                };
                stack.push(result);
            }else{
                int result = Integer.parseInt(ch);
                stack.push(result);
            }
        }
        return stack.pop();
    }

    class MinStack {

        Stack<Integer> stack ;
        Stack<Integer> minstack;

        public MinStack() {

            stack = new Stack<>();
            minstack = new Stack<>();
        }

        public void push(int val) {
            stack.push(val);
            if(minstack.empty()){
                minstack.push(val);
            } else if(val <= minstack.peek()){
                minstack.push(val);
            }

        }

        public void pop() {
            int val = stack.pop();
            if( minstack.peek()== val){
                minstack.pop();
            }

        }

        public int top() {
           return stack.peek();
        }

        public int getMin() {
            return minstack.peek();

        }
    }


}
