package use;
import operation.*;
import java.util.Scanner;


public class AdminUser extends User{

    private  Scanner sc = new Scanner(System.in);

    public AdminUser(String name) {
        super(name);
        initializeOperations(); // 构造时初始化一次
    }

    private void initializeOperations() {
        works[0] = new Exit();
        works[1] = new Show();
        works[2] = new Add();
        works[3] = new Deal();
        works[4] = new Find();
    }

    public int menu() {
        while(true) {
            System.out.println("0.退出程序");
            System.out.println("1.展示图书");
            System.out.println("2.增加图书");
            System.out.println("3.删除图书");
            System.out.println("4.查找图书");
            System.out.print("请选择功能：");
            try {
                int choice = sc.nextInt();
                if (choice >= 0 && choice <= 4) {
                    return choice;
                } else {
                    System.out.println("请输入0-4之间的数字");
                }
            } catch (Exception e) {
                System.out.println("请输入数字");
                sc.nextLine();
            }
        }
    }
}
