package com.salon.salon_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @Value("${MAIL_USERNAME}")
    private String username;

    @GetMapping("/mail")
    public String test() {
        return username;
    }
}
