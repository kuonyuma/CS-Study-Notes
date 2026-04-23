class Solution {
    public boolean validateStackSequences(int[] pushed, int[] popped) {
        Stack<Integer> stack = new Stack<>();
        int num = 0;
        for(int i = 0 ,j = 0;i < pushed.length;){
            num = pushed[i];

            if(num == popped[j]){
                j++;
                i++;
                while(!stack.isEmpty() &&stack.peek() == popped[j]){
                    j++;
                    stack.pop();
                }
            }else{
                stack.push(num);
                i++;
            }
        }

        return stack.isEmpty();

    }
}