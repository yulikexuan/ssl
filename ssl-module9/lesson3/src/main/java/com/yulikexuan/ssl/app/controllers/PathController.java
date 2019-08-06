//: com.yulikexuan.ssl.app.controllers.PathController.java


package com.yulikexuan.ssl.app.controllers;


import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Slf4j
@Controller
public class PathController {

    @RequestMapping("/login")
    public String login() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        log.info(">>>>>>> Principal: {}", authentication.getPrincipal());
        log.info(">>>>>>> Authorities: {}", authentication.getAuthorities());

        return "loginPage";
    }

}///:~