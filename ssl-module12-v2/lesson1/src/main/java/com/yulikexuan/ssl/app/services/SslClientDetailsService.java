//: com.yulikexuan.ssl.app.services.SslClientDetailsService.java


package com.yulikexuan.ssl.app.services;


import com.yulikexuan.ssl.domain.model.oauth2.Client;
import com.yulikexuan.ssl.domain.model.oauth2.ClientScope;
import com.yulikexuan.ssl.domain.model.oauth2.GrantType;
import com.yulikexuan.ssl.domain.services.oauth2.IClientService;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class SslClientDetailsService implements ClientDetailsService {

    static final String STRING_JOIN_DELIMITER = ",";

    private final IClientService clientService;

    public SslClientDetailsService(IClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws
            ClientRegistrationException {

        return this.clientService.getClientByClientId(clientId)
                .map(this::mapToClientDetails)
                .orElse(null);

                /*
        String scopes = client.getScope().stream().collect(Collectors.joining(","));
        String grantTypes = client.getAuthorizedGrantTypes().stream().collect(Collectors.joining(","));

        BaseClientDetails base = new BaseClientDetails(client.getClientId(), resourceIds, scopes, grantTypes, authorities);
        base.setClientSecret(client.getClientSecret());
        base.setAccessTokenValiditySeconds(client.getAccessTokenValiditySeconds());
        base.setRefreshTokenValiditySeconds(client.getRefreshTokenValiditySeconds());
        base.setAdditionalInformation(client.getAdditionalInformation());
        base.setAutoApproveScopes(client.getScope());
        return base;
                 */
    }

    private ClientDetails mapToClientDetails(Client client) {

        assert client != null;

        List<String> scopes = client.getScopes().stream()
                .map(ClientScope::getScope)
                .collect(Collectors.toList());

        List<String> grantTypes = client.getAuthorizedGrantTypes().stream()
                .map(GrantType::getType)
                .collect(Collectors.toList());

        BaseClientDetails base = new BaseClientDetails();

        base.setClientId(client.getClientId());
        base.setClientSecret(client.getClientSecret());
        base.setScope(scopes);
        base.setAuthorizedGrantTypes(grantTypes);
        base.setAutoApproveScopes(scopes);
        base.setAccessTokenValiditySeconds(client.getAccessTokenValiditySeconds());
        base.setRefreshTokenValiditySeconds(client.getRefreshTokenValiditySeconds());

        return base;
    }

}///:~