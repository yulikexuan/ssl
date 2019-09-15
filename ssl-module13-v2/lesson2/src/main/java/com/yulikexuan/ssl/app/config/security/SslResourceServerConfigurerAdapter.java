//: com.yulikexuan.ssl.app.config.security.ResourceServerConfigurerAdapter.java


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
        http.requestMatchers()
                .antMatchers("/api/users/**")
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET,"/api/users/**")
                .access("#oauth2.hasScope('PRIVILEGE_READ')")
                .antMatchers(HttpMethod.POST,"/api/users/**")
                .access("#oauth2.hasScope('PRIVILEGE_WRITE')")
                .antMatchers(HttpMethod.DELETE,"/api/users/**")
                .access("#oauth2.hasScope('PRIVILEGE_WRITE')");
    }

}///:~