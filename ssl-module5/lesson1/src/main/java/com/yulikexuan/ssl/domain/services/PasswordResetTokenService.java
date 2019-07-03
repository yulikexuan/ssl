//: com.yulikexuan.ssl.domain.services.PasswordResetTokenService.java


package com.yulikexuan.ssl.domain.services;


import com.yulikexuan.ssl.domain.model.PasswordResetToken;
import com.yulikexuan.ssl.domain.repositories.IPasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class PasswordResetTokenService implements IPasswordResetTokenService {

    private final IPasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    public PasswordResetTokenService(
            IPasswordResetTokenRepository passwordResetTokenRepository) {

        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    @Override
    public PasswordResetToken save(PasswordResetToken passwordResetToken) {
        return this.passwordResetTokenRepository.save(passwordResetToken);
    }

    @Override
    public Optional<PasswordResetToken> findByToken(String token) {
        return Optional.ofNullable(this.passwordResetTokenRepository
                .findByToken(token));
    }

}///:~