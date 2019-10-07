//: com.yulikexuan.ssl.ClientWmsApp.java


package com.yulikexuan.ssl;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.web.context.request.RequestContextListener;


@SpringBootApplication
public class ClientWmsApp extends SpringBootServletInitializer {

    /*
     * Spring-Boot is able to autowire Request/Session scoped beans into
     * filter's that are outside of the DispatcherServlet As per Spring's
     * documentation
     *
     * We need to add the RequestContextListener Bean or RequestContextFilter
     * Bean to enable this functionality
     *
     * To support the scoping of beans at the request, session, and global
     * session levels (web-scoped beans), some minor initial configuration is
     * required before you define your beans
     *
     * (This initial setup is not required for the standard scopes, singleton
     * and prototype.) ...
     *
     * If you access scoped beans within Spring Web MVC, in effect, within a
     * request that is processed by the Spring DispatcherServlet, or
     * DispatcherPortlet, then no special setup is necessary:
     * DispatcherServlet and DispatcherPortlet already expose all relevant state
     *
     */
    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

    public static void main(String[] args) {
        SpringApplication.run(ClientWmsApp.class, args);
    }

}///:~