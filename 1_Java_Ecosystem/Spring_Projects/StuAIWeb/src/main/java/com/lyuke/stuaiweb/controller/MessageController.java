package com.lyuke.stuaiweb.controller;

import com.lyuke.stuaiweb.model.Comment;
import com.lyuke.stuaiweb.model.Message;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.PostConstruct;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    // 使用线程安全的集合来模拟内存数据库
    private final List<Message> messages = new CopyOnWriteArrayList<>();

    // 初始化一些默认数据，让网页一打开就有内容
    @PostConstruct
    public void init() {
        Message msg1 = new Message("佚名树叶", "今天去看了日落，风很温柔。路边的野猫也对我喵喵叫，感觉生活还是充满小确幸的。", "碎碎念");
        msg1.getComments().add(new Comment("看风景的人", "哇，听起来好治愈，明天我也去江边走走！"));
        messages.add(msg1);
        
        messages.add(new Message("熬夜冠军", "这周的实验报告好难写，数据怎么都对不上。感觉整个人都emo了，好想回家吃妈妈做的菜...", "学业压力"));
        messages.add(new Message("追风筝的人", "如果你正在经历低谷，请相信，这只是人生长河中的一朵小小浪花。大声哭出来吧，哭完擦干眼泪，明天太阳依旧会升起。", "心情日记"));
        messages.add(new Message("橘子汽水", "终于买到了心仪已久的吉他！虽然手指按弦很痛，但是弹出完整旋律的那一刻，感觉一切都值得了。", "碎碎念"));
        messages.add(new Message("代码搬运工", "改了一个晚上的Bug，最后发现是少写了一个分号。我太难了。但是看到程序终于跑通，还是松了一口气。", "学业压力"));
    }

    // 获取所有留言
    @GetMapping
    public List<Message> getAllMessages() {
        return messages;
    }

    // 提交新留言
    @PostMapping
    public Map<String, Object> addMessage(@RequestBody Message newMessage) {
        Message message = new Message(newMessage.getAuthor(), newMessage.getContent(), newMessage.getTag());
        messages.add(0, message); // 新留言插到最前面
        return Map.of("success", true, "message", message);
    }

    // 提交新评论
    @PostMapping("/{id}/comments")
    public Map<String, Object> addComment(@PathVariable String id, @RequestBody Comment newComment) {
        Optional<Message> targetMessage = messages.stream().filter(m -> m.getId().equals(id)).findFirst();
        
        if (targetMessage.isPresent()) {
            Comment comment = new Comment(newComment.getAuthor(), newComment.getContent());
            targetMessage.get().getComments().add(comment);
            return Map.of("success", true, "comment", comment);
        } else {
            return Map.of("success", false, "error", "留言未找到");
        }
    }
}