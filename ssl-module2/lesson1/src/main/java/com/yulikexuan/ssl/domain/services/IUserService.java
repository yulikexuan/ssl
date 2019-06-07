//: com.yulikexuan.ssl.domain.services.IUserService.java


package com.yulikexuan.ssl.domain.services;


import com.yulikexuan.ssl.domain.model.User;

import java.util.List;
import java.util.Optional;


public interface IUserService {

    List<User> findAllUsers();

    Optional<User> getUserById(Long id);

    User saveUser(User user);

    void deleteUser(Long id);

}///:~