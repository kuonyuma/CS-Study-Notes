package com.ryuukee.verificationcode.verification;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/getCode")
public class getCode {
    private static final Logger logger = LoggerFactory.getLogger(getCode.class);
    //生成验证码
    @RequestMapping("/get")
    public void get(HttpServletResponse response, HttpSession session){
        Long start = System.currentTimeMillis();

        CircleCaptcha captcha = CaptchaUtil.createCircleCaptcha(200,
                100,
                4,
                20);
            try (ServletOutputStream out = response.getOutputStream()) {

                response.setContentType("image/jpeg");
                //禁止利用缓存
                response.setHeader("Pragma", "No-cache");
                captcha.write(out);

                session.setAttribute("key",captcha.getCode());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        logger.info(System.currentTimeMillis() - start+"");
    }

}

