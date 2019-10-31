//: com.yulikexuan.ssl.domain.respositories.oauth2.IGrantTypeRepository.java


package com.yulikexuan.ssl.domain.respositories.oauth2;


import com.yulikexuan.ssl.domain.model.oauth2.GrantType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface IGrantTypeRepository extends JpaRepository<GrantType, Long> {

}///:~