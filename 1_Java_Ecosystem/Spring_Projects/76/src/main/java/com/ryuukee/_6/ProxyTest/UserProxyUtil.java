package com.ryuukee._6.ProxyTest;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Slf4j
public class UserProxyUtil {

    public static User creatProxy(BigUser bigUser){
        return (User) Proxy.newProxyInstance(
            UserProxyUtil.class.getClassLoader(),// 将对象加载到内存中
            new Class[]{User.class},// 实现某个接口
            new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                    if(method.getName().equals("show") || method.getName().equals("login")){
                        long begin = System.currentTimeMillis();

                        method.invoke(bigUser,args);
                        long end = System.currentTimeMillis();
                        System.out.println(method.getName()+"耗时"+(end-begin)+"ms");
                    }

                    return null;
                }
            }
        );
    }
}
