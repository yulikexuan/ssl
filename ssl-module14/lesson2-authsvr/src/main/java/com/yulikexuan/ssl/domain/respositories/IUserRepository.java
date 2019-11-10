//: com.yulikexuan.ssl.domain.repositories.IUserRepository.java


package com.yulikexuan.ssl.domain.respositories;


import com.yulikexuan.ssl.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface IUserRepository extends JpaRepository<User, Long> {

    List<User> findByUsername(String username);
    List<User> findByEmail(String email);

}///:~