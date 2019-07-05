//: com.yulikexuan.ssl.domain.services.IUserService.java


package com.yulikexuan.ssl.domain.services;


import com.yulikexuan.ssl.domain.model.User;

import java.util.List;
import java.util.Optional;


public interface IUserService {

    long count();

    List<User> findAllUsers();

    Optional<User> getUserById(Long id);

    User saveUser(User user);

    User saveUser(User user, Long securityQuestionId, String answer);

    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByEmail(String email);

    Long getSecurityQuestionDefinitionIdByUser(User user);

    boolean isSecurityQuestionAnswerCorrect(User user, String answer);

    void deleteUser(Long id);

    void changeUserPassword(final User user, String password);

}///:~