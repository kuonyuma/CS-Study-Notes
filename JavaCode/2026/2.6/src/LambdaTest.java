import com.sun.source.tree.BreakTree;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class LambdaTest {

    public static void main1(String[] args) {

      Test test = (int a)->{
          System.out.println(a);
      };
      test.test(10);
    }

    public static void main2(String[] args) {
        PriorityQueue<String> heap = new PriorityQueue<>(
                ((String o1,String o2) -> o2.compareTo(o1) ));
        heap.offer("a");
        heap.offer("b");
        heap.offer("c");

    }

    public static void main3(String[] args) {
        Map<String, Integer> map = new HashMap<>();
        // 遍历键值对
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);

        map.forEach((key,val)
                -> System.out.println("key"+ key+" "+val+"val"));
    }

    public static void main(String[] args) {
        LambdaTest test = new LambdaTest();
        int sum = calculator(test::add, 1000, 7);
        System.out.println(sum);
    }

    public static int calculator(Operation operation, int a, int b){
        return operation.apply(a,b);
    }


    public int add(int a,int b){
       return a + b;
    }
    public static int sub(int a,int b){
        return a - b;
    }
    public static int multiplication(int a,int b){
        return a * b;
    }

}
@FunctionalInterface
interface Test{
    void test(int a);
}
interface Operation{
    int apply(int a,int b);
}

