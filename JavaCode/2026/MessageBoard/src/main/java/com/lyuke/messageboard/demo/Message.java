package com.lyuke.messageboard.demo;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Message{
    private String from;
    private String to;
    private String message;

}