package com.ryuukee.verificationcode.verification;

import jakarta.servlet.http.HttpSession;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/checkCode")
public class checkCode {

    @RequestMapping("/check")
    public boolean check(String userinsert, HttpSession httpsession){
        //检验验证码是否正确
        if(!StringUtils.hasLength(userinsert) || httpsession == null){
            return false;
        }

        String code = (String)httpsession.getAttribute("key");

        return code.equals(userinsert);
    }
}
