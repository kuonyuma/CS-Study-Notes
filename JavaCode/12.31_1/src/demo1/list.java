package demo1;
public interface list<T> {


    //遍历数组中的数字
    public void display();

    public void add(T num);
    public void add(int pos,T num);
    public void remove(int pos);
    public boolean contains(T value);
    public int indexOf(T value);
    public void clear();

    public int getSize();
    public void set(int pos,T value);
    boolean isEmpty();
}