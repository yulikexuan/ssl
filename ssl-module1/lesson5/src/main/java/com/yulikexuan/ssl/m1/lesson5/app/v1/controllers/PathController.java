//: com.yulikexuan.ssl.m1.lesson5.app.v1.controllers.PathController.java


package com.yulikexuan.ssl.m1.lesson5.app.v1.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class PathController {

    @RequestMapping("/login")
    public String list() {
        return "loginPage";
    }

}///:~