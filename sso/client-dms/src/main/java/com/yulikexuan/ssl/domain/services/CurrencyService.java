//: com.yulikexuan.ssl.domain.services.CurrencyService.java


package com.yulikexuan.ssl.domain.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Service;


@Service
public class CurrencyService extends OAuth2ResourceService
        implements ICurrencyService {

    @Autowired
    public CurrencyService(OAuth2RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    public String getExchangeRateInfomation() {

        String rateInfo = this.getRestTemplate().getForObject(
                "http://localhost:8089/currency/api/rates", String.class);

        return rateInfo;
    }

}///:~