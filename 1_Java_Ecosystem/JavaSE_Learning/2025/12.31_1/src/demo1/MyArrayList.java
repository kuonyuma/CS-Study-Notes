package demo1;
import java.util.Arrays;

public class MyArrayList<T> implements list<T> {
    private Object[] array;
    private int size;
    private  int CAPACITY  = 10;
    public MyArrayList(){
        array = new Object[CAPACITY];
        size = 0;//默认没有元素
    }
    public boolean chekCapacity(){
            return size == CAPACITY;
    }
    private void addCapacity(){
        if(chekCapacity()){
            CAPACITY *= 2;
            this.array = Arrays.copyOf(this.array, CAPACITY);
       }
    }
    private void chekPos(int pos){
        if(pos < 0 || pos >= size){
            throw new PosIllegal("pos坐标非法");
        }
    }
    private void chekPosAdd(int pos){
        if(pos < 0 || pos > size){
            throw new PosIllegal("pos坐标非法");
        }
    }
    @Override
    public void display (){
         if(size == 0){
             System.out.println("数组为空");
             return;
            }
         for (int i = 0; i < size; i++) {
            System.out.print(array[i] + " ");
        }

    }
    @Override
    public void add(T num) {//尾插
        addCapacity();//扩容
        array[size] = num;
        size++;
    }
    @Override
    public void add(int pos,T num){//任意位置插入
            chekPosAdd(pos);//这里有可能出现异常
            addCapacity();
            for (int i = size-1;pos <= i; i--) {
                array[i + 1] = array[i];
                array[pos] = num;
            }
        size++;
    }

    public void clear(){
        if(size == 0){
            return;
        }else{
            for (int i = 0; i < size; i++) {
                array[i] = null;
            }
            size = 0;
        }
    }
    public int getSize(){
        return size;
    }

    public void set(int pos,T value){
            chekPos(pos);
            array[pos] = value;
    }
    public T get(int pos){
            chekPos(pos);
            return (T)array[pos];
    }
    public int indexOf(T value){
        if(size == 0){
            return -1;
        }
        for (int i = 0; i < size; i++) {
                if(value == null ? array[i] == null : value.equals(array[i])) {
                    return i;
                }
            }
        return -1;
    }

    public boolean contains(T value) {
        for (int i = 0; i < size; i++) { // 只遍历有效元素
            if (value == null) {
                if (array[i] == null) {
                    return true;
                }
            } else {
                if (value.equals(array[i])) {
                    return true;
                }
            }
        }
        return false;
    }
    public void remove(int pos){
        try {
            chekPos(pos);
            for (int i = pos; i < size-1; i++) {
                array[i] = array[i+1];
            }
            array[size-1] = null;
            size--;
        } catch (PosIllegal e) {
            throw new PosIllegal();
        }
    }
    public boolean isEmpty(){
        return size==0;
    }

}
