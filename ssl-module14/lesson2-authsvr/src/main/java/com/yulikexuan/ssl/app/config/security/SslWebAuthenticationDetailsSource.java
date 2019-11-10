//: com.yulikexuan.ssl.app.config.security.SslWebAuthenticationDetailsSource.java


package com.yulikexuan.ssl.app.config.security;


import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;


@Component
public class SslWebAuthenticationDetailsSource implements
        AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> {

    @Override
    public WebAuthenticationDetails buildDetails(HttpServletRequest context) {
        return new SslWebAuthenticationDetails(context);
    }

}///:~