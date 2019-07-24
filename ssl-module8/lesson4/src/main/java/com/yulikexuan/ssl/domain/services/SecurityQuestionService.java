//: com.yulikexuan.ssl.domain.services.SecurityQuestionService.java


package com.yulikexuan.ssl.domain.services;


import com.yulikexuan.ssl.domain.model.SecurityQuestion;
import com.yulikexuan.ssl.domain.model.User;
import com.yulikexuan.ssl.domain.repositories.ISecurityQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class SecurityQuestionService implements ISecurityQuestionService {

    private final ISecurityQuestionRepository securityQuestionRepository;

    @Autowired
    public SecurityQuestionService(
            ISecurityQuestionRepository securityQuestionRepository) {

        this.securityQuestionRepository = securityQuestionRepository;
    }

    @Override
    public Optional<SecurityQuestion> findByUser(User user) {
        return Optional.ofNullable(this.securityQuestionRepository.findByUser(user));
    }

    @Override
    public Optional<SecurityQuestion> findByUserAndAnswer(User user, String answer) {
        return Optional.ofNullable(this.securityQuestionRepository
                .findByUserAndAnswer(user, answer));
    }

    @Override
    public SecurityQuestion save(SecurityQuestion securityQuestion) {
        return this.securityQuestionRepository.save(securityQuestion);
    }

}///:~