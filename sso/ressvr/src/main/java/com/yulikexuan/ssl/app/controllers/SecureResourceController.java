//: com.yulikexuan.ssl.app.controllers.SecureResourceController.java


package com.yulikexuan.ssl.app.controllers;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/res")
public class SecureResourceController {

    @GetMapping("/secure")
    public String secureResource() {
        return "You are reading our secure documentation ... ";
    }

}///:~