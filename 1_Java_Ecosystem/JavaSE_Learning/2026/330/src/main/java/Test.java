import java.util.Scanner;

public class Test {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        int num = in.nextInt();
        System.out.println("第一次输入");
        in.nextLine();
        String str2 = in.nextLine();
        System.out.println("第二次输入");

        char frist_name = in.nextLine().charAt(0);
        System.out.println(num);
        System.out.println(str2);

        System.out.println(frist_name);
    }
}
