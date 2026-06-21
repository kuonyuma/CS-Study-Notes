import java.util.Comparator;

class compara implements Comparator<Integer> {
    @Override
    public int compare(Integer o1, Integer o2) {
        return o2 - o1;
    }
}

public class MyPriorityQueue   {
    int[] array;
    int usedSize;
    public MyPriorityQueue(){
        array = new int[10];
    }
    //创建大根堆的时间复杂度是O(n)
    public void initHeap(int[] array){
        for (int i = 0; i < array.length; i++) {
            this.array[i] = array[i];
        }
        usedSize = array.length;
        siftDown(this.array, usedSize);
    }
    //向下调整的时间复杂度是O(logn)
    private void siftDown(int[] array,int usedSize){

        for (int parents = (usedSize-2)/2; parents >= 0 ; parents--) {

            int child = 2*parents+1;

            while(child < usedSize){
                int p = parents;

                if(child+1 <usedSize && array[child] < array[child+1]){
                    child = child + 1;
                }

                if(array[child]>array[p]){

                    int tmp = array[p];
                    array[p] = array[child];
                    array[child] = tmp;

                    p = child;
                    child = 2*child+1;

                }else{
                    break;
                }
            }
        }
    }
    //插入N个元素的时间复杂度是O(Nlogn)
    public void offer(int val){
        array[usedSize] =val;
        siftUp(usedSize);
        usedSize++;
    }
    //插入一个元素的时间复杂度是O(logn)
    private void siftUp(int child){
        int  parents = (child-1) / 2;
        while(parents>0){
            if(array[parents] < array[child]){

                int tmp = array[parents];
                array[parents] = array[child];
                array[child] = tmp;

                child = parents;
                parents = (parents - 1) / 2;

            }else{

                break;
            }

        }
    }
    //删除一个元素的时间复杂度是O(logn)
    public int poll(){

        int top = array[0];
        array[0] = array[usedSize-1];
        array[usedSize-1] = top;
        usedSize--;
        if(usedSize > 0) {
            siftDown(this.array,usedSize);
        }
        return top;
    }
    //排序，将数组的元素从小到达
    public int[] heapSort(int[] array){
        initHeap(array);
        int count = usedSize;
        while(count > 1){
            int top = this.array[0];
            this.array[0] = this.array[count-1];
            this.array[count-1] = top;
            count--;
            siftDown(this.array,count);

        }
        return this.array;
    }

    //TOPK问题

}
