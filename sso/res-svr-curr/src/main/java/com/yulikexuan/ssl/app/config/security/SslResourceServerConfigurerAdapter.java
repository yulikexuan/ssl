//: com.yulikexuan.ssl.app.config.security.SslResourceServerConfigurerAdapter.java


package com.yulikexuan.ssl.app.config.security;


import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
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

    @Value("classpath:sslkey.pub")
    Resource sslPublicKeyResource;

    private final DefaultAccessTokenConverter sslAccessTokenConverter;

    @Autowired
    public SslResourceServerConfigurerAdapter(
            DefaultAccessTokenConverter sslAccessTokenConverter) {

        this.sslAccessTokenConverter = sslAccessTokenConverter;
    }

    // Beans:

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {

        final JwtAccessTokenConverter converter = new JwtAccessTokenConverter();

        // In case there is additional custom claims in JWT token
        converter.setAccessTokenConverter(this.sslAccessTokenConverter);

        // Set up public key as verifier key into the jwt access token converter
        String sslkey = null;
        try {
            sslkey = IOUtils.toString(sslPublicKeyResource.getInputStream(),
                    Charset.forName("UTF-8"));
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

    /*
     * The DefaultTokenServices implements AuthorizationServerTokenServices,
     * ResourceServerTokenServices, ConsumerTokenServices, InitializingBean
     *
     * The DefaultTokenServices is a base implementation for token services
     * using random UUID values for the access token and refresh token values.
     * The main extension point for customizations is the TokenEnhancer which
     * will be called after the access and refresh tokens have been generated
     * but before they are stored.
     * Persistence is delegated to a TokenStore implementation and customization
     * of the access token to a TokenEnhancer
     */
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
    }


}///:~