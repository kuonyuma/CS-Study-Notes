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
            try {
                int choice = sc.nextInt();
                if (choice > 0 && choice < works.length) {
                    return choice;
                } else {
                    System.out.println("请注意输出返回");
                }
            } catch (Exception e) {
                System.out.println("请输入数组");
                sc.nextLine();
            }
        }
    }
}
