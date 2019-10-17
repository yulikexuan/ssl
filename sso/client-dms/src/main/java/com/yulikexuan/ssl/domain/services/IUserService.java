//: com.yulikexuan.ssl.domain.services.IUserService.java


package com.yulikexuan.ssl.domain.services;


import com.yulikexuan.ssl.app.model.SslOAuth2AuthenticationDto;


public interface IUserService {

    SslOAuth2AuthenticationDto getAuthentication();

}///:~