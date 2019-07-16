//: com.yulikexuan.ssl.app.controllers.PathController.java


package com.yulikexuan.ssl.app.controllers;


import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Slf4j
@Controller
public class PathController {

    @RequestMapping("/login")
    public String list() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        log.info(">>>>>>> Principal: {}", authentication.getPrincipal());
        log.info(">>>>>>> Authorities: {}", authentication.getAuthorities());

        return "loginPage";
    }

    // Avoid 404 error on /favicon.ico
    @GetMapping("favicon.ico")
    @ResponseBody
    public void returnNoFavicon() {
    }


}///:~