package com.lyuke.captchademo.Code;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@ConfigurationProperties(prefix = "code")
@Data
@Configuration
public class Code {
    private int height;
    private int width;
}
