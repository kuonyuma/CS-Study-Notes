public class Master {
    public static void main(String[] args) {
        Machine machine = new Machine();
        try {
            machine.buy(4,1);
        } catch (MyException e) {
            throw new RuntimeException(e);
        }finally{
            System.out.println("修复错误");
        }
    }
}
