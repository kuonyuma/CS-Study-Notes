package com.ryuukee._6.demo4;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/demo4")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/normal")
    public String testNormal(
            @RequestParam(defaultValue = "苹果") String product,
            @RequestParam(defaultValue = "1") int qty) {
        return orderService.createOrder(product, qty);
    }

    @GetMapping("/bad")
    public String testException() {
        return orderService.createBadOrder("违禁品");
    }
}
