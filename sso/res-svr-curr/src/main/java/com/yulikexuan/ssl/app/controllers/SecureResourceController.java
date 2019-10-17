//: com.yulikexuan.ssl.app.controllers.SecureResourceController.java


package com.yulikexuan.ssl.app.controllers;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class SecureResourceController {

    @GetMapping("/welcome")
    public String greeting() {
        return "Welcome to Currency Exchange Resources! ";
    }

    @GetMapping("/rates")
    public String secureResource() {
        return "You are reading our secure rates ... ";
    }

}///:~