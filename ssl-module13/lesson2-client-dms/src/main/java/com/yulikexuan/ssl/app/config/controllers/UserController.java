//: com.yulikexuan.ssl.app.config.controllers.UserController.java


package com.yulikexuan.ssl.app.config.controllers;


import com.yulikexuan.ssl.app.model.SslOAuth2AuthenticationDto;
import com.yulikexuan.ssl.domain.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/currentUser")
    public ModelAndView currentUser() {
        SslOAuth2AuthenticationDto principal =
                this.userService.getCurrentUserInfo();
        return new ModelAndView("currentUserPage",
                "principal", principal);
    }

}///:~