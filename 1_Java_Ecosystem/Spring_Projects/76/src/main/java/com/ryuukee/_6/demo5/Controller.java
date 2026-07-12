package com.ryuukee._6.demo5;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo5")
public class Controller {

    @Autowired
    private Student stu;

    // GET /demo5/play?game=篮球
    @RequestMapping("/play")
    public void play(@RequestParam(defaultValue = "篮球") String game) {
        stu.play(game);
    }

    // GET /demo5/doWork?subject=数学&hours=2
    @RequestMapping("/doWork")
    public String doWork(
            @RequestParam(defaultValue = "数学") String subject,
            @RequestParam(defaultValue = "2") int hours) {
        return stu.doWork(subject, hours);
    }
}
