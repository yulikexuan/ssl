//: com.yulikexuan.ssl.app.controllers.LogoutController.java


package com.yulikexuan.ssl.app.controllers;


import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
@Controller
public class LogoutController {

    @RequestMapping("/exit")
    public void exit(HttpServletRequest request, HttpServletResponse response) {

        // token can be revoked here if needed
        new SecurityContextLogoutHandler().logout(request, null,
                null);
        try {
            String requestReferrer = request.getHeader("referer");
            log.info(">>>>>>> Request Referrer: {}", requestReferrer);
            //sending back to client app
            response.sendRedirect("http://localhost:8082/dms");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}///:~