//: com.yulikexuan.ssl.app.controllers.AuthenticationController.java


package com.yulikexuan.ssl.app.controllers;


import com.yulikexuan.ssl.app.model.SslOAuth2AuthenticationDto;
import com.yulikexuan.ssl.domain.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;


@Slf4j
@Controller
public class AuthenticationController {

    private UserService userService;
    private OAuth2RestTemplate restTemplate;

    @Autowired
    public AuthenticationController(UserService userService,
                                    OAuth2RestTemplate restTemplate) {

        this.userService = userService;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/authentication")
    public ModelAndView getAuthentication(final Principal authentication) {

        log.info(">>>>>>> Organization: {}",
                this.restTemplate.getAccessToken().getAdditionalInformation()
                        .get("organization"));

        SslOAuth2AuthenticationDto principal =
                this.userService.getAuthentication();

        return new ModelAndView("currentUserPage",
                "principal", principal);
    }

}///:~