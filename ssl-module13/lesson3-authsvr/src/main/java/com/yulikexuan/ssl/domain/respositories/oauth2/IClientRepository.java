//: com.yulikexuan.ssl.domain.respositories.oauth2.IClientRepository.java


package com.yulikexuan.ssl.domain.respositories.oauth2;


import com.yulikexuan.ssl.domain.model.oauth2.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface IClientRepository extends
        JpaRepository<Client, Long> {

    List<Client> findByClientId(String clientId);

}///:~