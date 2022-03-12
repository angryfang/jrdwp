package com.example.server;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author angryfun
 * @date 2022/3/9
 */
@RestController
public class TestController {
    @GetMapping("/hello")
    public String hello(@RequestParam String name) {
        return "hello, " + name;
    }
}
