//: com.yulikexuan.ssl.app.controllers.AuthenticationController.java


package com.yulikexuan.ssl.app.controllers;


import com.yulikexuan.ssl.app.model.SslOAuth2AuthenticationDto;
import com.yulikexuan.ssl.domain.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class AuthenticationController {

    private UserService userService;

    @Autowired
    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/authentication")
    public ModelAndView getAuthentication() {
        SslOAuth2AuthenticationDto principal =
                this.userService.getAuthentication();
        return new ModelAndView("currentUserPage",
                "principal", principal);
    }

}///:~