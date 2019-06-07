//: com.yulikexuan.ssl.domain.repositories.IUserRepository.java


package com.yulikexuan.ssl.domain.repositories;


import com.yulikexuan.ssl.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface IUserRepository extends JpaRepository<User, Long> {

}///:~