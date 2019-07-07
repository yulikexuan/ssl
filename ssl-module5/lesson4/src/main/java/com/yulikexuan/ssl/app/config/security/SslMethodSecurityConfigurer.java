//: com.yulikexuan.ssl.app.config.security.SslMethodSecurityConfigurer.java


package com.yulikexuan.ssl.app.config.security;


import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;


@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true) // prePostEnabled is REQUIRED!
public class SslMethodSecurityConfigurer extends
        GlobalMethodSecurityConfiguration {

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        return new SslMethodSecurityExpressionHandler();
    }

}///:~