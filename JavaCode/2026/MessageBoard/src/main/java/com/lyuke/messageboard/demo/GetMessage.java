package com.lyuke.messageboard.demo;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping( "/GetMessage")
public class GetMessage {
    List<Message> list = new ArrayList<>();
    @RequestMapping("/Get")
    public String Get( Message message){
        if(StringUtils.hasLength(message.getFrom())&&
                StringUtils.hasLength(message.getTo())&&
                StringUtils.hasLength(message.getMessage())){
            list.add(message);
            synchronized(this){
                return "已经成功留言";
            }
        }
        return"留言格式不合法";
    }

    @RequestMapping("/chekMessage")
    public List<Message> chekMessage(){
        if(!list.isEmpty())
            return list;
        return null;
    }

}
