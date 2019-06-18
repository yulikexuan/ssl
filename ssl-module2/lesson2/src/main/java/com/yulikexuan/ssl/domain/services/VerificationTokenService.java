//: com.yulikexuan.ssl.domain.services.VerificationTokenService.java


package com.yulikexuan.ssl.domain.services;


import com.yulikexuan.ssl.domain.model.VerificationToken;
import com.yulikexuan.ssl.domain.repositories.IVerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class VerificationTokenService implements IVerificationTokenService {

    private final IVerificationTokenRepository verificationTokenRepository;

    @Autowired
    public VerificationTokenService(
            IVerificationTokenRepository verificationTokenRepository) {

        this.verificationTokenRepository = verificationTokenRepository;
    }

    @Override
    public Optional<VerificationToken> findByToken(String token) {
        return Optional.ofNullable(
                this.verificationTokenRepository.findByToken(token));
    }

}///:~