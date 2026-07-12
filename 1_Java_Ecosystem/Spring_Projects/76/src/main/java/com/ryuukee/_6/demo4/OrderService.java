package com.ryuukee._6.demo4;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderService {


    public String createOrder(String productName, int quantity) {
        log.info("【OrderService】正在创建订单：商品={}, 数量={}", productName, quantity);
        return "订单创建成功！商品：" + productName + "，数量：" + quantity;
    }

    public String createBadOrder(String productName) {
        log.info("【OrderService】尝试创建异常订单，商品={}", productName);
        if ("违禁品".equals(productName)) {
            throw new IllegalArgumentException("不能购买违禁品：" + productName);
        }
        return "不应该到这里";
    }
}
