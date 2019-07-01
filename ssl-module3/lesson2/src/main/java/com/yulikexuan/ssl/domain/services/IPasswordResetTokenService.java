//: com.yulikexuan.ssl.domain.services.IPasswordResetTokenService.java


package com.yulikexuan.ssl.domain.services;


import com.yulikexuan.ssl.domain.model.PasswordResetToken;

import java.util.Optional;


public interface IPasswordResetTokenService {

    PasswordResetToken save(PasswordResetToken passwordResetToken);
    Optional<PasswordResetToken> findByToken(String token);

}///:~