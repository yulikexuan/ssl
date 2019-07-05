//: com.yulikexuan.ssl.domain.repositories.IPasswordResetTokenRepository.java


package com.yulikexuan.ssl.domain.repositories;


import com.yulikexuan.ssl.domain.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface IPasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, Long> {

    PasswordResetToken findByToken(String token);

}///:~