//: com.yulikexuan.ssl.domain.model.IUserToken.java


package com.yulikexuan.ssl.domain.model;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;


public interface IUserToken {

    default Date calculateExpiryDate(final int expiryTimeInMinutes) {
        final LocalDateTime dateTimeNow = LocalDateTime.now();
        dateTimeNow.plusMinutes(expiryTimeInMinutes);
        return Date.from(dateTimeNow.atZone(ZoneId.systemDefault()).toInstant());
    }

}///:~