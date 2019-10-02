//: com.yulikexuan.ssl.app.config.security.SslTokenEnhancer.java


package com.yulikexuan.ssl.app.config.security;


import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.Map;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;


public class SslTokenEnhancer implements TokenEnhancer {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken,
                                     OAuth2Authentication authentication) {

        Map<String, Object> additionalInfo = Map.of("organization",
                authentication.getName() + randomAlphabetic(4));

        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(
                additionalInfo);

        return accessToken;
    }

}///:~