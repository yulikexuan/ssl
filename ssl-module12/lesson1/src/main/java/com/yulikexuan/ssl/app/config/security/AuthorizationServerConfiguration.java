//: com.yulikexuan.ssl.app.config.security.AuthorizationServerConfiguration.java


package com.yulikexuan.ssl.app.config.security;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;


@Slf4j
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration
        extends AuthorizationServerConfigurerAdapter {

    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthorizationServerConfiguration(
            AuthenticationManager authenticationManager) {

        super();

        this.authenticationManager = authenticationManager;
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

        endpoints.tokenStore(tokenStore()).
                authenticationManager(this.authenticationManager).
                allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);
    }

    @Bean
    public TokenStore tokenStore() {
        return new InMemoryTokenStore();
    }

}///:~