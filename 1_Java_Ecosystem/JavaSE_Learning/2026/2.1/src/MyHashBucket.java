import java.util.Objects;
public class MyHashBucket<T ,V> {
    //节点
    static class Node<T,V>{
        public T key;
        public V val;
        public Node<T,V> next;

        public Node(T key,V val){
            this.key = key;
            this.val = val;
        }
    }

    @SuppressWarnings("unchecked")
    Node<T,V>[] array =(Node<T, V>[]) new Node[10];
    int usedSize;
    int capacity = 10;
    private static final float LOAD = 0.75f;

    //头插入
    public void offer(T key,V val){
        if(key == null){
            return;
        }
        int index = Math.abs(key.hashCode()) % array.length;
        Node<T,V> cur = array[index];
        while(cur != null){
            if(cur.key.equals(key)){
                cur.val = val;
                return;
            }
            cur = cur.next;
        }

        if(usedSize * 1.0 / array.length >= LOAD){
            reSize();
        }

        Node<T,V> newnode = new Node<>(key,val);
        newnode.next = array[index];
        array[index] = newnode;
        usedSize++;
    }
    //扩容，其实就是重新将节点offer到新的数组中
    private void reSize(){
        //扩容后的数组
        @SuppressWarnings("unchecked")
       Node<T,V>[] newArray = (Node<T, V>[]) new Node[capacity*=2];
       //遍历每一个节点
       for(Node<T,V> newNode :array){

               Node<T,V> cur = newNode;
               while(cur != null){

                   Node<T,V> curN = cur.next;
                   int newindex = Math.abs(cur.key.hashCode()) % capacity;
                   cur.next = newArray[newindex];
                   newArray[newindex] = cur;
                   cur = curN;
               }

       }
       array = newArray;
    }
    public V get(T key){

        if(key == null){
            return null;
        }
        int index = Math.abs(key.hashCode()) % array.length;
        Node<T,V> cur = array[index];
        while(cur != null){
            if(cur.key.equals(key)){
                return cur.val;
            }
            cur = cur.next;
        }
        return null;
    }

}
