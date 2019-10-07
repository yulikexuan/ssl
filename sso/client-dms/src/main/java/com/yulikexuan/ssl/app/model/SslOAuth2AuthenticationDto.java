//: com.yulikexuan.ssl.app.model.SslOAuth2AuthenticationDto.java


package com.yulikexuan.ssl.app.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class SslOAuth2AuthenticationDto {

    private UserDto userDto;
    private boolean authenticated;
    private String tokenValue;
    private String tokenType;
    private String sessionId;
    private String organization;
    private String name;

}///:~