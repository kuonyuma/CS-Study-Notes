package com.ryuukee._6.ProxyTest;

public class Main {
    public static void main(String[] args) {

        BigUser bigUser = new BigUser();
        User proxy = UserProxyUtil.creatProxy(bigUser);
        proxy.login("admin","123456");
        proxy.show();
    }
}
