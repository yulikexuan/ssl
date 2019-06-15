//: com.yulikexuan.ssl.app.config.security.AuthorizationServerConfiguration.java


package com.yulikexuan.ssl.app.config.security;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.token.DefaultToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.security.KeyPair;


@Slf4j
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfigurer
        extends AuthorizationServerConfigurerAdapter {

    @Value("${signing-key:oui214hmui23o4hm1pui3o2hp4m1o3h2m1o43}")
    private String signingKey;

    private final KeyPair keyPair;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    @Autowired
    public AuthorizationServerConfigurer(KeyPair keyPair,
            AuthenticationManager authenticationManager,
            UserDetailsService userDetailsService) {

        super();

        this.keyPair = keyPair;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {

        final JwtAccessTokenConverter jwtAccessTokenConverter =
                new JwtAccessTokenConverter();

        jwtAccessTokenConverter.setSigningKey(signingKey);

        return jwtAccessTokenConverter;
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {

        DefaultTokenServices tokenServices = new DefaultTokenServices();

        tokenServices.setTokenStore(this.tokenStore());
        tokenServices.setSupportRefreshToken(true);

        return tokenServices;
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

        clients.inMemory()
                .withClient("client")
                .secret("{noop}123456")
                .authorizedGrantTypes("password", "refresh_token")
                .refreshTokenValiditySeconds(3600 * 24) // One Day
                .scopes("resources", "read", "write", "trust")
                .autoApprove("resources")
                .accessTokenValiditySeconds(3600); // One Hour
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints)
            throws Exception {

        endpoints.tokenStore(tokenStore())
                .authenticationManager(this.authenticationManager)
                .userDetailsService(this.userDetailsService)
                .accessTokenConverter(this.accessTokenConverter())
                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.checkTokenAccess("permitAll()");
        super.configure(security);
    }

}///:~