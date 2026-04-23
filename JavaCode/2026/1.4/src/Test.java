import java.util.Iterator;
import java.util.Stack;

public class Test {
    public static void main(String[] args) {
      MyStack<Integer> stack = new MyStack<>();
        stack.push(10);
        stack.push(20);
        stack.push(30);
        stack.push(40);
       int a =  stack.search(1);
        System.out.println(a);
    }
}
