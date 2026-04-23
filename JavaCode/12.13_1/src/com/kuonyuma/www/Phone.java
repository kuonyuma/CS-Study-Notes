package com.kuonyuma.www;

public class Phone{
    public String phoneName;//用户可以自己给手机命名
    private String screen;//
    final String cpu;//每一个对象有独立的cpu
    private int battery;
    //完全自定义手机的参数
    public Phone(String pname,String screen,String cpu
            ,int battery){
        this.phoneName = pname;
        this.screen = screen;
        this.cpu = cpu;
        if(this.battery>100){
            this.battery = 100;
        }
        else if(this.battery<0){
            this.battery = 0;
        }
        else{
            this.battery = battery;
        }
    }
    //自动给出手机默认配置
    public Phone(){
        this("默认名称","LCD材质","A16",100);
    }
    //给出部分参数
    public Phone(String name){
        this(name,"LCD材质","A16",100);
    }
    //展示一些手机出场自带的部件名称
    public void show(){
        System.out.println("名称"+phoneName+" "+"屏幕"+screen
                +" "+"处理器"+cpu+" "+"电池"+battery);
    }
    //玩手机
    public void playPhone(){
        if(battery>20){
            battery -= 20;
        }
        else {
            System.out.println("手机电量不足");
        }
    }
    public void setScreen(String screen){
        this.screen = screen;
    }
    public String getScreen(){
        return this.screen;
    }
}



