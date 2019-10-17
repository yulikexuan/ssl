//: com.yulikexuan.ssl.app.config.security.SslResourceServerConfigurerAdapter.java


package com.yulikexuan.ssl.app.config.security;


import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.io.IOException;
import java.nio.charset.Charset;


@Configuration
@EnableResourceServer
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SslResourceServerConfigurerAdapter
        extends ResourceServerConfigurerAdapter {

    private final Environment environment;
    private final SslAccessTokenConverter sslAccessTokenConverter;

    @Autowired
    public SslResourceServerConfigurerAdapter(
            Environment environment,
            SslAccessTokenConverter sslAccessTokenConverter) {

        this.environment = environment;
        this.sslAccessTokenConverter = sslAccessTokenConverter;
    }

    // Beans:

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        final JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        Resource resource = new ClassPathResource("sslkey.pub");
        String sslkey = null;
        try {
            sslkey = IOUtils.toString(resource.getInputStream(), Charset.forName("UTF-8"));
        } catch (final IOException ioe) {
            throw new RuntimeException(ioe);
        }
        converter.setVerifierKey(sslkey);
        return converter;
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(this.accessTokenConverter());
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(this.tokenStore());
        tokenServices.setSupportRefreshToken(true);
        return tokenServices;
    }

    // Configuration:

    @Override
    public void configure(ResourceServerSecurityConfigurer resourcesConfigure)
            throws Exception {

        resourcesConfigure.tokenServices(this.tokenServices());
    }

    // HTTP security concerns:

    @Override
    public void configure(final HttpSecurity http) throws Exception {

        http.requestMatchers()
                .antMatchers("/api/**")
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/api/welcome")
                .permitAll()
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/api/rates")
                .access("#oauth2.hasScope('PRIVILEGE_READ')")
                .anyRequest()
                .permitAll()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                .csrf()
                .disable();

//        http.authorizeRequests()
//                .antMatchers("/api/welcome")
//                .permitAll()
//                .anyRequest()
//                .authenticated()
//                .and()
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .csrf()
//                .disable();
    }


}///:~