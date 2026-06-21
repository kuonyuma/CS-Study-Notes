import java.util.LinkedList;
import java.util.Queue;

class MyStack {

    //用两个队列来模拟实现栈
    Queue<Integer> q1;
    Queue<Integer> q2;

    public MyStack() {
         q1 = new LinkedList<>();
         q2 = new LinkedList<>();
    }

    public void push(int x) {
        if(empty()){
            q1.offer(x);
        }else if(!q1.isEmpty()){
            q1.offer(x);
        }else{
            q2.offer(x);
        }

    }

    public int pop() {
        if(empty()){
            return -1;
        }else if(!q1.isEmpty()){
            int size = q1.size();
            for (int i = 0; i <size -1; i++) {
                q2.offer(q1.poll());
            }
            return q1.poll();
        }else{
            int size = q2.size();
            for (int i = 0; i <size-1; i++) {
                q1.offer(q2.poll());
            }
            return q2.poll();
        }


    }

    public int top() {
        if(empty()){
            return -1;
        }else if(!q1.isEmpty()){
            int val = 0;
            int size = q1.size();
            for (int i = 0; i <size; i++) {
                val = q1.poll();
                q2.offer(val);
            }
            return val;
        }else{
            int val = 0 ;
            int size = q2.size();
            for (int i = 0; i <size; i++) {
                val = q2.poll();
                q1.offer(val);
            }
            return val;
        }
    }

    public boolean empty() {
        return q1.isEmpty()&& q2.isEmpty();

    }
}
