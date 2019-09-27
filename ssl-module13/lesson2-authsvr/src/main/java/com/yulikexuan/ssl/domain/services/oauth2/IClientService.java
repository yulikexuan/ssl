//: com.yulikexuan.ssl.domain.services.oauth2.IClientService.java


package com.yulikexuan.ssl.domain.services.oauth2;


import com.yulikexuan.ssl.domain.model.oauth2.Client;

import java.util.List;
import java.util.Optional;


public interface IClientService {

    long count();

    List<Client> findAllClientDetails();

    Optional<Client> getClientByClientId(String clientId);

    Client save(Client client);

    Optional<String> getClientHomeUri(String requestReferrer);

}///:~