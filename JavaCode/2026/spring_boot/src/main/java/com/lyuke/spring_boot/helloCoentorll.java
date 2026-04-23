package com.lyuke.spring_boot;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;

@RequestMapping("/class")
@RestController
public class helloCoentorll {
    @RequestMapping("hello/he/h")
    public String sayHello() {

        return "Hello, World! 可以GET和 POST";
    }
    //只能接受GET
    @GetMapping("M2")
    public String M2(){

        return "M2 只能接收GET";
    }
    @PostMapping("M3")
    public String M3() {

        return "M3 只能接受POST";
    }

    @RequestMapping(value = "M4",method = RequestMethod.POST)
    public String M4(){

        return "M4 可POST";
    }

    @RequestMapping(value = "M5",method = RequestMethod.GET)
        public String M5(){
        return "M5 可GET";
    }
    //传递单个参数
    @RequestMapping("/M6")
    public String M6(String str){
        return "str = " + str;
    }
    //传递多个参数
    @RequestMapping("/M7")
    public String M7(String name,Integer age){
        return "name = "+ name + ", age = " + age;
    }
    //传递多个参数,使用基本类型
    @RequestMapping("/M8")
    public String M8(String name,int age){
        return "name = "+ name + ", age = " + age;
    }
    //传递自定义类型
    @RequestMapping("/M9")
    public String M9(User user){
        return user.toString();
    }
    //传递数组
    @RequestMapping("/M10")
    public String M10(String[] arr) {
        StringBuilder sb = new StringBuilder();
        for (String s : arr) {
            sb.append(s).append(" ");
        }
        return sb.toString();
    }
    //传递数组
    @RequestMapping("/M11")
    public String M11(String[] arr) {
        return Arrays.toString(arr);
    }
    //改名字
    @RequestMapping("/M12")
    public String M12(@RequestParam("name") String username) {
        return "name = " + username;
    }
    //传递集合
    @RequestMapping("/M13")
    public String M13(@RequestParam(value = "list",required = false) ArrayList<String> list) {
        if(list == null){
            return null;
        }
        return list.toString();
    }
    //学习QequestParam
    @RequestMapping("/M14")
    public String M14(@RequestParam(value = "key",
            required = false,defaultValue = "0"
           )Integer num) {

        return "num = " + ++num;
    }
}
