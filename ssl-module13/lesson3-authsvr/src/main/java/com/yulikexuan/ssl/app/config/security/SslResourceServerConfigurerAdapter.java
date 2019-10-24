//: com.yulikexuan.ssl.app.config.security.SslResourceServerConfigurerAdapter.java

package com.yulikexuan.ssl.app.config.security;


import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;


@Configuration
@EnableResourceServer
public class SslResourceServerConfigurerAdapter extends
        ResourceServerConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {

        /*
         * A HttpSecurity is similar to Spring Security's XML <http> element in
         * the namespace configuration
         *
         * It allows configuring web based security for specific http requests
         *
         * By default it will be applied to all requests, but can be restricted
         * using requestMatcher(RequestMatcher) or other similar methods
         *
         * To enable #oAuth2 security expressions,
         * like "#oauth2.hasScope('PRIVILEGE_READ')",
         * it is only needed to set default expression handler as
         * OAuth2MethodSecurityExpressionHandler instead of
         * DefaultMethodSecurityExpressionHandler because
         * OAuth2MethodSecurityExpressionHandler extends it anyway then the
         * whole previous functionality remains the same
         * So, we should override createExpressionHandler() method of
         * GlobalMethodSecurityConfiguration
         * But, if add dependency "spring-security-oauth2-autoconfigure" to pom,
         * overriding createExpressionHandler() method is no needed
         * https://stackoverflow.com/questions/29797721/oauth2-security-expressions-on-method-level
         */
        http.requestMatchers()
                // Restrict config only to "/api/users/**"
                .antMatchers("/api/users/**")
                .and()
                // Allows restricting access based upon the HttpServletRequest using
                // Allows specifying that URLs are secured by an arbitrary expression
                .authorizeRequests()

                .antMatchers(HttpMethod.GET,"/api/users/**")
                .access("#oauth2.hasScope('PRIVILEGE_READ')")

                .antMatchers(HttpMethod.POST,"/api/users/**")
                .access("#oauth2.hasScope('PRIVILEGE_WRITE')")

                .antMatchers(HttpMethod.DELETE,"/api/users/**")
                .access("#oauth2.hasScope('PRIVILEGE_WRITE')");
    }

}///:~