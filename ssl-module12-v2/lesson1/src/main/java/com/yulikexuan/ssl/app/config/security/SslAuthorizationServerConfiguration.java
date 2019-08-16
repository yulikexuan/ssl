//: com.yulikexuan.ssl.app.config.security.SslAuthorizationServerConfiguration.java


package com.yulikexuan.ssl.app.config.security;


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


@Configuration
@EnableAuthorizationServer
public class SslAuthorizationServerConfiguration extends
        AuthorizationServerConfigurerAdapter {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SslAuthorizationServerConfiguration(
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder) {

        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    // Beans:

    @Bean
    public TokenStore tokenStore() {
        return new InMemoryTokenStore();
    }

    // Configuration:

    @Override
    public void configure(final AuthorizationServerEndpointsConfigurer endpoints)
            throws Exception {

        endpoints.tokenStore(this.tokenStore())
                .authenticationManager(this.authenticationManager)
                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);
    }

    @Override
    public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("cloud")
                .secret(this.passwordEncoder.encode("2PGlgRk9Mv"))
                .authorizedGrantTypes("password")
                // Scope name: <app-name>.<resource-uri>.<privilege>
                // For example: product.list
                .scopes("any")
                // Not redirected and promoted to manually approve any scopes
                .autoApprove(true)
                .accessTokenValiditySeconds(3600);
    }

}///:~