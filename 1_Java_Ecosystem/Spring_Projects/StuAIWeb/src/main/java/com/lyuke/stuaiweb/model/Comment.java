package com.lyuke.stuaiweb.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Comment {
    private String id;
    private String author;
    private String content;
    private String date;

    public Comment() {
    }

    public Comment(String author, String content) {
        this.id = UUID.randomUUID().toString();
        this.author = author == null || author.trim().isEmpty() ? "匿名路人" : author;
        this.content = content;
        this.date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}