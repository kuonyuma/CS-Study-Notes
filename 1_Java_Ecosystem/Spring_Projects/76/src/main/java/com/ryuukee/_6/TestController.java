package com.ryuukee._6;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/Test")
public class TestController {

    @RequestMapping("t1")
    public void t1(){
        log.info("t1测试完毕");
    }

}
