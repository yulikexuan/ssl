//: com.yulikexuan.ssl.domain.services.IVerificationTokenService.java


package com.yulikexuan.ssl.domain.services;


import com.yulikexuan.ssl.domain.model.VerificationToken;

import java.util.Optional;


public interface IVerificationTokenService {

    Optional<VerificationToken> findByToken(String token);

}///:~