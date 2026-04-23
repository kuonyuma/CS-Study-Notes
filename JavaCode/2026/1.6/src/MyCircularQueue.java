

class MyCircularQueue {

    int[]array;
    int head = 0;
    int last = 0;
    int capacity;
    int size =0;
    public MyCircularQueue(int k) {
        array = new int[k];
        capacity = k;

    }

    public boolean enQueue(int value) {
        if(isFull()){
            return false;
        }
        array[last] = value;
        last = (last +1)% capacity;
        size++;
         return true;
    }

    public boolean deQueue() {
        if(isEmpty()){
            return false;
        }
        head = (head + 1)% capacity;
        size --;
        return true;
    }

    public int Front() {
        if(head == last){
            return -1;
        }else{
            return array[head];
        }
    }

    public int Rear() {
       if(isEmpty()){
           return -1;
        }else {
            int index = (last -1 +capacity) % capacity;
            return array[index];
        }
    }

    public boolean isEmpty() {
            return head == last;
    }

    public boolean isFull() {
        return (last+1)%capacity == head;
    }
}
