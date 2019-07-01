//: com.yulikexuan.ssl.domain.services.ISecurityQuestionService.java


package com.yulikexuan.ssl.domain.services;


import com.yulikexuan.ssl.domain.model.SecurityQuestion;
import com.yulikexuan.ssl.domain.model.User;

import java.util.Optional;


public interface ISecurityQuestionService {

    Optional<SecurityQuestion> findByUser(User user);
    Optional<SecurityQuestion> findByUserAndAnswer(User user, String answer);
    SecurityQuestion save(SecurityQuestion securityQuestion);

}///:~