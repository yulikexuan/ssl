//: com.yulikexuan.ssl.domain.repositories.IVerificationTokenRepository.java


package com.yulikexuan.ssl.domain.repositories;


import com.yulikexuan.ssl.domain.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface IVerificationTokenRepository
        extends JpaRepository<VerificationToken, Long> {

    VerificationToken findByToken(String token);

}///:~