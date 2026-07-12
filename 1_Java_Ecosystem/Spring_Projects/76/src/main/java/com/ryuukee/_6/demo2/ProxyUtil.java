package com.ryuukee._6.demo2;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ProxyUtil {

    public static Star creatProxy(BigStar bigStar){

        return (Star) Proxy.newProxyInstance(
            ProxyUtil.class.getClassLoader(),
            new Class[]{Star.class},
            new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                    if(method.getName().equals("sing")||
                        method.getName().equals("dance")) {

                        System.out.println("代理人预处理中");

                        Object ob = method.invoke(bigStar,args);

                        System.out.println("代理人正在处理后事");
                        return ob;
                    }
                    return null;
                }
            }
        );
    }
}
