//: com.yulikexuan.ssl.domain.services.UserService.java


package com.yulikexuan.ssl.domain.services;


import com.yulikexuan.ssl.app.model.SslOAuth2AuthenticationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class UserService extends OAuth2ResourceService implements IUserService {

    @Autowired
    public UserService(OAuth2RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    public SslOAuth2AuthenticationDto getAuthentication() {

        Map<String, Object> additionalInfo = this.getAccessToken()
                .getAdditionalInformation();

        String org = additionalInfo.get("organization").toString();

        SslOAuth2AuthenticationDto principal = this.getRestTemplate().getForObject(
                "http://localhost:8081/ums/api/users/current",
                SslOAuth2AuthenticationDto.class);

        assert org.equals(principal.getOrganization());

        return principal;
    }

}///:~