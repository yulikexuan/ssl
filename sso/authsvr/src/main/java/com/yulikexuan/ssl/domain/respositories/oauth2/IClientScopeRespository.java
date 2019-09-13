//: com.yulikexuan.ssl.domain.respositories.oauth2.IClientScopeRespository.java


package com.yulikexuan.ssl.domain.respositories.oauth2;


import com.yulikexuan.ssl.domain.model.oauth2.ClientScope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface IClientScopeRespository extends
        JpaRepository<ClientScope, Long> {

}///:~