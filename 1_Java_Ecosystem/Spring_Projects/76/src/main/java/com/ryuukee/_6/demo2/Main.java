package com.ryuukee._6.demo2;

public class Main {

    public static void main(String[] args) {


        BigStar bigStar = new BigStar("nonoka");

        Star star = ProxyUtil.creatProxy(bigStar);

        star.dance();
        String str = star.sing("雨爱");
        System.out.println(str);

    }
}
