//: com.yulikexuan.ssl.app.config.filters.SslLoggingFilter.java


package com.yulikexuan.ssl.app.config.filters;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;


@Component
@Slf4j
public class SslLoggingFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        final HttpServletRequest httpServletRequest =
                (HttpServletRequest) request;

        String url = httpServletRequest.getRequestURL().toString();
        String queryString = Optional
                .ofNullable(httpServletRequest.getQueryString())
                .map(value -> "?" + value)
                .orElse("");

        log.info(">>>>>>> Applying SslLoggingFilter for URL: {}{}", url,
                queryString);

        chain.doFilter(request, response);

    }

}///:~