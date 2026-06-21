import java.util.Arrays;


public class MyStack<T> {
    private Object[] array;
    private int size;
    private int Capacity;

    public MyStack(){
        Capacity = 10;
        size = 0;
        array = new Object[Capacity];
    }
    public boolean isValid(String s){
        //遍历整个字符串数组
        MyStack<Character> stack = new MyStack<>();
        for (int i = 0; i < s.length(); i++) {
            //将左括号拿出来
            char ch = s.charAt(i);
            if(ch=='('||ch=='{'||ch=='['){
                stack.push(ch);
            }else{
                if(stack.empty()){
                    return  false;
                }char ch2 = stack.peek();
                if(ch ==']'&&ch2=='['||ch == ')' && ch2 == '(' ||ch == '}'&&ch2 =='{'){
                    stack.pop();
                }else{
                    return false;
                }
            }
        }
        if(!stack.empty()){
            return false;
        }
        return true;

    }
    public void push(T val){
        if(isFull()){
            array = Arrays.copyOf(array,2*array.length);
        }
        array[size++] = val;
    }
    private boolean isFull(){
        return size == Capacity;
    }
    public boolean empty(){
        return size == 0;
    }
    public void pop(){
        if(size == 0){
            return;
        }
        System.out.println(array[--size]);
    }
    public T peek(){
        return  (T)array[size -1];
    }
    public int search(T obj){
        int count= size;
        while(count >0){
            if(array[count -1] == obj){
                return size - (count -1);
            }else{
                count--;
            }
        }
        return -1;
    }



}

