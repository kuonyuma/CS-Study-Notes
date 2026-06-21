package use;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class UserManager {
    private final Map<String, Account> users = new HashMap<>();

    public UserManager(){
        // 统一使用 "admin" 作为身份标识
        users.put("admin", new Account("admin", "123123", "admin"));
    }

    public boolean register(String name, String pass, String type){
        if(users.containsKey(name)){
            return false;
        }
        users.put(name, new Account(name, pass, type));
        return true;
    }

    public User login(){
        System.out.print("请输入账号名称:");
        Scanner sc = new Scanner(System.in);
        String name = sc.nextLine();
        System.out.print("请输入密码：");
        String password = sc.nextLine();
        
        Account account = users.get(name);
        if(account != null && account.getPassword().equals(password)) {
            if (account.getType().equals("admin")) {
                return new AdminUser(name);
            } else {
                return new NormalUser(name);
            }
        }
        System.out.println("用户名或密码错误");
        return null;
    }
}