package com.lyuke.captchademo.CaptCha;


import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.lyuke.captchademo.Code.Code;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/HutoolCaptCha")
public class HutoolCaptCha {

    private final Code codeBean;

    public HutoolCaptCha(Code codeBean) {
        this.codeBean = codeBean;
    }

    //生成验证码
    @RequestMapping("/SetCode")
    public void SetCode(
            HttpSession session,
            HttpServletResponse response) throws IOException {

        LineCaptcha lineCaptcha =
                CaptchaUtil.createLineCaptcha(
                        codeBean.getWidth(),
                        codeBean.getHeight());

        long milliTimestamp = java.time.Instant.now().toEpochMilli();
        session.setAttribute("key_time",milliTimestamp);

        String code = lineCaptcha.getCode();

        session.setAttribute("key",code);

        System.out.println("验证码是" + lineCaptcha.getCode());

        lineCaptcha.write( response.getOutputStream());


    }

    //判断验证码
    @RequestMapping("/judgeCode")
    public boolean judgeCOde(
            HttpSession session,
            @RequestParam("clientInput") String clientInput){

        String code = (String)session.getAttribute("key");
        
        long newTime = java.time.Instant.now().toEpochMilli();
        long oldTime = (long)session.getAttribute("key_time");

        if(code.equals(clientInput) && (newTime - oldTime) < 60 * 1000L){
            return true;
        }

        return false;
    }

}
