//: com.yulikexuan.ssl.app.events.SslLoggedOutSuccessHandler.java


package com.yulikexuan.ssl.app.events;


import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;


@Slf4j
@Component
public class SslLoggedOutSuccessHandler implements LogoutSuccessHandler {

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onLogoutSuccess(
            HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {

        Optional.ofNullable(request.getSession(false))
                .ifPresent((httpSession) ->
                        httpSession.removeAttribute("loggedInUser"));

        final String targetUrl = "/login";

        if (response.isCommitted()) {
            log.debug(">>>>>>> Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        this.redirectStrategy.sendRedirect(request, response, targetUrl);
    }

}///:~