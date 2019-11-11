//: com.yulikexuan.ssl.app.config.security.SslUrlAuthenticationFailureHandler.java


package com.yulikexuan.ssl.app.config.security;


import com.yulikexuan.ssl.app.services.SmsService;
import com.yulikexuan.ssl.domain.model.User;
import com.yulikexuan.ssl.domain.services.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
public class SslUrlAuthenticationFailureHandler
        extends SimpleUrlAuthenticationFailureHandler {

    private final IUserService userService;
    private final SmsService smsService;

    public SslUrlAuthenticationFailureHandler(IUserService userService,
                                              SmsService smsService) {

        super();
        this.userService = userService;
        this.smsService = smsService;
    }

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception)
            throws IOException, ServletException {

        if (NullVerificationCodeException.class.isInstance(exception)) {
            log.info(">>>>>>> Processing verification code request ... ...");
            String username = request.getParameter("username");
            User user = this.userService.findUserByUsername(username).get();
            this.smsService.sendVerificationCode(user);
            getRedirectStrategy().sendRedirect(request, response,
                    "/verificationcode");
        } else {
            super.onAuthenticationFailure(request, response, exception);
        }
    }

}///:~