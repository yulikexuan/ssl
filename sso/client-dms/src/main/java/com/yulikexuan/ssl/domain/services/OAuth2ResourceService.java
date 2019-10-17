//: com.yulikexuan.ssl.domain.services.OAuth2ResourceService.java


package com.yulikexuan.ssl.domain.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.OAuth2AccessToken;


public abstract class OAuth2ResourceService {

    private final OAuth2RestTemplate restTemplate;

    public OAuth2ResourceService(OAuth2RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    protected OAuth2AccessToken getAccessToken() {
        return this.restTemplate.getAccessToken();
    }

    protected OAuth2RestTemplate getRestTemplate() {
        return this.restTemplate;
    }

}///:~