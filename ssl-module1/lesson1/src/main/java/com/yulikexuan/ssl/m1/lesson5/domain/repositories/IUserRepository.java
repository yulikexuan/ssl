//: com.yulikexuan.ssl.m1.lesson5.domain.repositories.IUserRepository.java


package com.yulikexuan.ssl.m1.lesson5.domain.repositories;


import com.yulikexuan.ssl.m1.lesson5.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface IUserRepository extends JpaRepository<User, Long> {

}///:~