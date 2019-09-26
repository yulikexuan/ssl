//: com.yulikexuan.ssl.app.config.security.OAuthClientConfig.java


package com.yulikexuan.ssl.app.config.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;


@Configuration
public class OAuthClientConfig {

    private OAuth2ClientContext clientContext;

    @Autowired
    public OAuthClientConfig(@Qualifier("oauth2ClientContext")
            OAuth2ClientContext clientContext) {

        this.clientContext = clientContext;
    }

    @Bean
    public OAuth2RestTemplate oauth2RestTemplate(
            final OAuth2ProtectedResourceDetails details) {

        return new OAuth2RestTemplate(details, this.clientContext);
    }

}///:~