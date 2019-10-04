//: com.yulikexuan.ssl.app.config.security.SslSecurityConfigerAdapter.java


package com.yulikexuan.ssl.app.config.security;


import io.micrometer.core.instrument.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.io.IOException;


@Slf4j
@Configuration
@EnableOAuth2Sso
@Order(value = 0)
public class SslSecurityConfigerAdapter extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {

        http.antMatcher("/**")
                .authorizeRequests()
                .antMatchers("/", "/login")
                .permitAll()
                .anyRequest()
                .authenticated()

                .and()
                .logout()
                .logoutSuccessUrl("http://localhost:8081/ums/exit")
                .and()

                .csrf()
                .disable();
    }

}///:~