//: com.yulikexuan.ssl.app.controllers.LogoutController.java


package com.yulikexuan.ssl.app.controllers;


import com.yulikexuan.ssl.domain.model.oauth2.Client;
import com.yulikexuan.ssl.domain.services.oauth2.IClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
@Controller
public class LogoutController {

    private final IClientService clientService;

    @Autowired
    public LogoutController(IClientService clientService) {
        this.clientService = clientService;
    }

    @RequestMapping("/exit")
    public void exit(HttpServletRequest request, HttpServletResponse response) {

        // token can be revoked here if needed
        new SecurityContextLogoutHandler().logout(request, null,
                null);
        try {
            String requestReferrer = request.getHeader("referer");
            String homeUri = this.clientService.getClientHomeUri(requestReferrer)
                    .orElseThrow();
            response.sendRedirect(homeUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}///:~