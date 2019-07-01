//: com.yulikexuan.ssl.domain.repositories.ISecurityQuestionRepository.java


package com.yulikexuan.ssl.domain.repositories;


import com.yulikexuan.ssl.domain.model.SecurityQuestion;
import com.yulikexuan.ssl.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ISecurityQuestionRepository
        extends JpaRepository<SecurityQuestion, Long> {

    SecurityQuestion findByUser(User user);
    SecurityQuestion findByUserAndAnswer(User user, String answer);

}///:~