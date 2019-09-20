//: com.yulikexuan.ssl.domain.services.oauth2.ClientService.java


package com.yulikexuan.ssl.domain.services.oauth2;


import com.yulikexuan.ssl.domain.model.oauth2.Client;
import com.yulikexuan.ssl.domain.respositories.oauth2.IClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class ClientService implements IClientService {

    private final IClientRepository clientDetailsRepository;

    @Autowired
    public ClientService(
            IClientRepository clientDetailsRepository) {

        this.clientDetailsRepository = clientDetailsRepository;
    }

    @Override
    public long count() {
        return this.clientDetailsRepository.count();
    }

    @Override
    public List<Client> findAllClientDetails() {
        return this.clientDetailsRepository.findAll();
    }

    @Override
    public Optional<Client> getClientByClientId(String clientId) {
        return this.clientDetailsRepository.findByClientId(clientId)
                .stream()
                .findAny();
    }

    @Override
    public Client save(Client client) {
        return this.clientDetailsRepository.save(client);
    }

}///:~