public class Test {

    public static void main(String[] args) {
        LinkedList test = new LinkedList();

        test.addLast(10);
        boolean a = test.findData(20);
        System.out.println(a);

        test.display();

    }
}
