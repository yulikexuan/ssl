//: com.yulikexuan.ssl.domain.services.UserService.java


package com.yulikexuan.ssl.domain.services;


import com.yulikexuan.ssl.app.model.SslOAuth2AuthenticationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class UserService {

    private final OAuth2RestTemplate restTemplate;

    @Autowired
    public UserService(OAuth2RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public SslOAuth2AuthenticationDto getAuthentication() {

        Map<String, Object> additionalInfo = this.restTemplate.getAccessToken()
                .getAdditionalInformation();

        String org = additionalInfo.get("organization").toString();

        SslOAuth2AuthenticationDto principal = this.restTemplate.getForObject(
                "http://localhost:8081/ums/api/users/me",
                SslOAuth2AuthenticationDto.class);

        assert org.equals(principal.getOrganization());

        return principal;
    }

    public OAuth2AccessToken getAccessToken() {
        return this.restTemplate.getAccessToken();
    }

}///:~