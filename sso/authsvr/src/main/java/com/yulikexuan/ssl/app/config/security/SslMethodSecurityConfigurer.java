//: com.yulikexuan.ssl.app.config.security.SslMethodSecurityConfigurer.java


package com.yulikexuan.ssl.app.config.security;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;


@Slf4j
@Configuration
// prePostEnabled is REQUIRED!
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SslMethodSecurityConfigurer extends
        GlobalMethodSecurityConfiguration {

    @Override
    protected AccessDecisionManager accessDecisionManager() {

        final AffirmativeBased accessDecisionManager =
                (AffirmativeBased) super.accessDecisionManager();

        accessDecisionManager.getDecisionVoters().add(PrivilegeVoter.create());

        return accessDecisionManager;
    }

}///:~