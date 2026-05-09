package com.lyuke.stuaiweb.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class Message {
    private String id;
    private String author;
    private String content;
    private String date;
    private String tag;
    private int likes;
    private List<Comment> comments = new CopyOnWriteArrayList<>();

    public Message() {
    }

    public Message(String author, String content, String tag) {
        this.id = UUID.randomUUID().toString();
        this.author = author == null || author.trim().isEmpty() ? "匿名路人" : author;
        this.content = content;
        this.date = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        this.tag = tag == null || tag.trim().isEmpty() ? "碎碎念" : tag;
        this.likes = (int) (Math.random() * 50); // 随机生成一些点赞数增加真实感
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
    
    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }

    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }
}
