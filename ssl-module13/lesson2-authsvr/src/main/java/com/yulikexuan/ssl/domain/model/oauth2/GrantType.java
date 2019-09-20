//: com.yulikexuan.ssl.domain.model.oauth2.GrantType.java


package com.yulikexuan.ssl.domain.model.oauth2;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Data
@Entity
@NoArgsConstructor
@Builder @AllArgsConstructor
public class GrantType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type;

}///:~