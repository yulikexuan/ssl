//: com.yulikexuan.ssl.app.config.security.SslTokenEnhancer.java


package com.yulikexuan.ssl.app.config.security;


import com.google.common.collect.ImmutableMap;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.Map;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;


/*
 * Add an extra field “organization” to Access Token – with this SslTokenEnhancer
 */
public class SslTokenEnhancer implements TokenEnhancer {

    static final String ORG_KEY = "organization";
    static final int SECURED_ORG_NAME_LENGTH = 8;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken,
                                     OAuth2Authentication authentication) {

        String securedOrgName = String.format("%1$s_%2$s",
                authentication.getName(),
                randomAlphabetic(SECURED_ORG_NAME_LENGTH));

        /*
         * Additional information that token granters would like to add to the
         * token, e.g. to support new token types.
         *
         * If the values in the map are primitive then remote communication is
         * going to always work.
         *
         * It should also be safe to use maps (nested if desired), or something
         * that is explicitly serializable by Jackson.
         */
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(
                ImmutableMap.of(ORG_KEY, securedOrgName));

        return accessToken;
    }

}///:~