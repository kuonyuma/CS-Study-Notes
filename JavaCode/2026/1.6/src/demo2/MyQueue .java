package demo2;

import java.util.Stack;

class MyQueue {
//用两个栈来实现队列
    Stack<Integer> s1;
    Stack<Integer> s2;

 public MyQueue() {
    s1 = new Stack<>();
    s2 = new Stack<>();

 }

 public void push(int x) {
    s1.push(x);
 }

public int pop() {
     if(empty()){
         return -1;
     }else if(s2.empty()){
         while(!s1.empty()){
             s2.push(s1.pop());
         }
         return s2.pop();
     }else{
         return s2.pop();
     }
}

public int peek() {
    if(empty()){
        return -1;
    }else if(s2.empty()){
        while(!s1.empty()){
            s2.push(s1.pop());
        }
        return s2.peek();
    }else{
        return s2.peek();
    }

}

public boolean empty() {
     return s1.empty()&&s2.empty();
}

}