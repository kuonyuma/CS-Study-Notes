package com.ryuukee._6.ProxyTest;

public class BigUser implements User{

    private boolean flg;



    @Override
    public void login(String name,String pass){
        if("admin".equals(name) && "123456".equals(pass)){
            System.out.println("登录成功");
            flg = true;
            return;
        }
        System.out.println("登录失败");
    }

    @Override
    public void show(){
        if(flg){
            System.out.println("用户以阅读过....");
        }else{
            System.out.println("你暂无访问权限");
        }
    }


}
