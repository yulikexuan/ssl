//: com.yulikexuan.ssl.domain.model.oauth2.Client.java


package com.yulikexuan.ssl.domain.model.oauth2;


import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Data
@Entity
@NoArgsConstructor
@Builder @AllArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String clientId;

    private String clientSecret;

    @Singular
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true,
            fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id")
    private Set<GrantType> authorizedGrantTypes = new HashSet<>();

    @Singular
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true,
            fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id")
    private Set<ClientScope> scopes = new HashSet<>();

    private boolean autoApprove;

    private Integer accessTokenValiditySeconds = 3600;
    private Integer refreshTokenValiditySeconds = 3600 * 24;
    private String redirectUris;

}///:~