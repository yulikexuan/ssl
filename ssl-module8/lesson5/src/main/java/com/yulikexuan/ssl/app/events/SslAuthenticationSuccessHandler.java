//: com.yulikexuan.ssl.app.events.SslAuthenticationSuccessHandler.java


package com.yulikexuan.ssl.app.events;


import com.yulikexuan.ssl.app.model.ActiveUserStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;


@Slf4j
@Component
public class SslAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ActiveUserStore activeUserStore;
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Autowired
    public SslAuthenticationSuccessHandler(ActiveUserStore activeUserStore) {
        this.activeUserStore = activeUserStore;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        Optional.ofNullable(request.getSession(false))
                .ifPresent((httpSession) -> {
                    LoggedInUserListener loggedInUser =
                            new LoggedInUserListener(authentication.getName(),
                                    this.activeUserStore);
                    httpSession.setAttribute("loggedInUser", loggedInUser);
                });

        final String targetUrl = "/";

        if (response.isCommitted()) {
            log.debug(">>>>>>> Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        this.redirectStrategy.sendRedirect(request, response, targetUrl);
    }

}///:~