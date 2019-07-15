//: com.yulikexuan.ssl.app.config.security.SslMethodSecurityConfigurer.java


package com.yulikexuan.ssl.app.config.security;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.intercept.RunAsManager;
import org.springframework.security.access.intercept.RunAsManagerImpl;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;


@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true) // prePostEnabled is REQUIRED!
public class SslMethodSecurityConfigurer extends
        GlobalMethodSecurityConfiguration {

    @Value("${app.security.runAskey}")
    private String runAsKey;

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        return new SslMethodSecurityExpressionHandler();
    }

    /*
     * RunAsManager permits implementations to replace the Authentication object
     * that applies to the current secure object invocation only like "Generate report"
     */
    @Override
    protected RunAsManager runAsManager() {
        // super.runAsManager() returns null by default
        final RunAsManagerImpl runAsManager = new RunAsManagerImpl();
        runAsManager.setKey(this.runAsKey);
        return runAsManager;
    }

}///:~