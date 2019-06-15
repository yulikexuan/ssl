//: com.yulikexuan.ssl.app.controllers.WelcomeController.java


package com.yulikexuan.ssl.app.controllers;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/res")
public class WelcomeController {

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to Spring Security Lesson - Module 12 !";
    }

}///:~