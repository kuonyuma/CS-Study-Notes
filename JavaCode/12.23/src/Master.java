import user.Admin;
import user.NormalUse;
import user.User;
import java.security.spec.RSAOtherPrimeInfo;
import java.sql.SQLOutput;
import java.util.Scanner;

public class Master {

    public static User login(){
     //登录系统
        System.out.println("请输入你的姓名：");
        Scanner sc = new Scanner(System.in);
        String name = sc.nextLine();
        System.out.println("输入你的权限");
        int cass =  sc.nextInt();
        if(cass == 1){
            return new Admin(name);//将名字一起传给user
        }else{
            return new NormalUse(name);
        }


    }
    public static void main(String[] args) {
        //登陆，选哪一个权限的菜单
        User use = login();
        while(true){
            //将menu调啊出来，
            //系统会向上转型
            use.menu();
        }
    }

}
