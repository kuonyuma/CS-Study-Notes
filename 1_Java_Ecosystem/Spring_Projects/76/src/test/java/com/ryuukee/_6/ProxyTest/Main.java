package com.ryuukee._6.ProxyTest;

import com.ryuukee._6.demo1.ProxyTest.BigUser;
import com.ryuukee._6.demo1.ProxyTest.User;
import com.ryuukee._6.demo1.ProxyTest.UserProxyUtil;

public class Main {
    public static void main(String[] args) {

        BigUser bigUser = new BigUser();
        User proxy = UserProxyUtil.creatProxy(bigUser);
        proxy.login("admin","123456");
        proxy.show();
    }
}
