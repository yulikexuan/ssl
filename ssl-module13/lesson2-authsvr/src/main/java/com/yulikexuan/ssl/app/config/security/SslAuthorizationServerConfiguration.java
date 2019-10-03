//: com.yulikexuan.ssl.app.config.security.SslAuthorizationServerConfiguration.java


package com.yulikexuan.ssl.app.config.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.util.List;


@Configuration
@EnableAuthorizationServer
public class SslAuthorizationServerConfiguration extends
        AuthorizationServerConfigurerAdapter {

    public static final String KEY_STORE_ALIAS = "sslsso";
    public static final String KEY_STORE_FILE_NAME = "sslsso.jks";

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final ClientDetailsService clientDetailsService;
    private final UserDetailsService userDetailsService;

//    @Value("${app.security.oauth2.jwt.signingkey}")
//    private String signingKey = "6264BB136A72A461C3ACCFB2FC1BF";

    @Autowired
    public SslAuthorizationServerConfiguration(
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            ClientDetailsService clientDetailsService,
            UserDetailsService userDetailsService) {

        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.clientDetailsService = clientDetailsService;
        this.userDetailsService = userDetailsService;
    }

    // Beans:

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {

        final JwtAccessTokenConverter accessTokenConverter =
                new JwtAccessTokenConverter();

        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(
                new ClassPathResource(KEY_STORE_FILE_NAME),
                KEY_STORE_ALIAS.toCharArray());

        accessTokenConverter.setKeyPair(keyStoreKeyFactory.getKeyPair(
                KEY_STORE_ALIAS));

        // accessTokenConverter.setSigningKey(signingKey);

        return accessTokenConverter;
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(this.accessTokenConverter());
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenService() {
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(this.tokenStore());
        tokenServices.setSupportRefreshToken(true);
        return tokenServices;
    }

    @Bean
    public TokenEnhancer tokenEnhancer() {
        return new SslTokenEnhancer();
    }

    // Configuration:

    @Override
    public void configure(final AuthorizationServerEndpointsConfigurer endpoints)
            throws Exception {

        JwtAccessTokenConverter tokenConverter = this.accessTokenConverter();
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> tokenEnhancers =
                List.of(this.tokenEnhancer(), tokenConverter);
        tokenEnhancerChain.setTokenEnhancers(tokenEnhancers);

        endpoints.tokenStore(this.tokenStore())
                .tokenEnhancer(tokenEnhancerChain)
                .authenticationManager(this.authenticationManager)
                .userDetailsService(this.userDetailsService)
                .accessTokenConverter(this.accessTokenConverter())
                .allowedTokenEndpointRequestMethods(HttpMethod.POST);
    }

    @Override
    public void configure(final ClientDetailsServiceConfigurer clients) throws
            Exception {

        clients.withClientDetails(this.clientDetailsService);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws
            Exception {

        security.tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()")
                .allowFormAuthenticationForClients(); // This can disable "Basic Auth"

        super.configure(security);
    }

}///:~