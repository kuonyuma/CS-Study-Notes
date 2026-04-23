package demo1;

public class Master {
    public static void main(String[] args) {
        // 包装类
        Integer a = 1;
        System.out.println(a);
        int b = a;
        System.out.println(b);

        demo11<Integer> test = new demo11<>();
        test.setArr(0, 1);
        System.out.println(test.getArr(0));

    }
}
