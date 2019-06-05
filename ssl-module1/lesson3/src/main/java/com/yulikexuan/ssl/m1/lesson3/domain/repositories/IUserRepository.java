//: com.yulikexuan.ssl.m1.lesson3.domain.repositories.IUserRepository.java


package com.yulikexuan.ssl.m1.lesson3.domain.repositories;


import com.yulikexuan.ssl.m1.lesson3.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface IUserRepository extends JpaRepository<User, Long> {

}///:~