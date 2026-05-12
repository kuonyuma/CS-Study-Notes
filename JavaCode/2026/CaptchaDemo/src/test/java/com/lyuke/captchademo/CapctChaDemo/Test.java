package com.lyuke.captchademo.CapctChaDemo;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class Test {
    public static void main(String[] args) {
        LineCaptcha lineCaptcha =
                CaptchaUtil.createLineCaptcha(50, 50);
        System.out.println(lineCaptcha.getCode());

    }

    //生成验证码
    public static void SetCode(
            HttpSession sseion,
            HttpServletResponse response) {
        LineCaptcha lineCaptcha =
                CaptchaUtil.createLineCaptcha(50, 50);
        System.out.println(lineCaptcha.getCode());
    }
}
