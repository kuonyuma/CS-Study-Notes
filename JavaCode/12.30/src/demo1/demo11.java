package demo1;

public class demo11<T extends Number> {

    private Object[] arr = new Object[10];

    @SuppressWarnings("unchecked")
    public T getArr(int pos) {
        return (T) arr[pos];
    }

    public void setArr(int pos, T num) {
        this.arr[pos] = num;
    }

}
