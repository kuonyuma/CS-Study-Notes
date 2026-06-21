package demo2;

class MyCircularQueue {
    //浪费一个空间
    int[] array;
    int head;
    int last;
    int capacity;

    public MyCircularQueue(int k) {
        array = new int[k+1];
        capacity = k+1;
        head = last = 0;
    }

    public boolean enQueue(int value) {
        if(isFull()){
            return false;
        }
        array[last] = value;
        last = (last+1)%capacity;
        return true;
    }

    public boolean deQueue() {
        if(isEmpty()){
            return false;
        }
         head = (head + 1)%capacity;
        return true;
    }

    public int Front() {
        if(isEmpty()){
            return -1;
        }
        return array[head];
    }

    public int Rear() {
        if(isEmpty()){
            return -1;
        }
        return array[(last-1+capacity)%capacity];

    }

    public boolean isEmpty() {
        return last == head;

    }

    public boolean isFull() {
        return (last+1)%capacity == head;
    }
}