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

//    @Override
//    protected MethodSecurityMetadataSource customMethodSecurityMetadataSource() {
//
//        Map<String, List<ConfigAttribute>> methodMap = new HashMap<>();
//
//        // A customized AccessDecisionVoter for Privileges should be defined if
//        // creating SecurityConfig with Privileges other than Roles
//        methodMap.put("com.yulikexuan.ssl.app.controllers.UserController.createForm",
//                SecurityConfig.createList("PRIVILEGE_CREATE"));
//        methodMap.put("com.yulikexuan.ssl.app.controllers.UserController.delete",
//                SecurityConfig.createList("PRIVILEGE_DELETE"));
//        methodMap.put("com.yulikexuan.ssl.app.controllers.UserController.modifyForm",
//                SecurityConfig.createList("PRIVILEGE_WRITE"));
//
//        return new MapBasedMethodSecurityMetadataSource(methodMap);
//    }

    @Override
    protected AccessDecisionManager accessDecisionManager() {
        final AffirmativeBased accessDecisionManager =
                (AffirmativeBased) super.accessDecisionManager();
        accessDecisionManager.getDecisionVoters().add(PrivilegeVoter.create());
        return accessDecisionManager;
    }

}///:~