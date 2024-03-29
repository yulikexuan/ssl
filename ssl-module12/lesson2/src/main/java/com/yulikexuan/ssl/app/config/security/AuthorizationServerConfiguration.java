//: com.yulikexuan.ssl.app.config.security.AuthorizationServerConfiguration.java


package com.yulikexuan.ssl.app.config.security;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;


@Slf4j
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration
        extends AuthorizationServerConfigurerAdapter {

    @Value("${signing-key:oui214hmui23o4hm1pui3o2hp4m1o3h2m1o43}")
    private String signingKey;

    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthorizationServerConfiguration(
            AuthenticationManager authenticationManager) {

        super();

        this.authenticationManager = authenticationManager;
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

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

        clients.inMemory()
                .withClient("client")
                .secret("{noop}123456")
                .authorizedGrantTypes("password")
                .scopes("resources")
                .autoApprove("resources")
                .accessTokenValiditySeconds(3600);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints)
            throws Exception {

        endpoints.tokenStore(tokenStore())
                .authenticationManager(this.authenticationManager)
                .accessTokenConverter(this.accessTokenConverter())
                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);
    }

}///:~