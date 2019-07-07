//: com.yulikexuan.ssl.domain.model.SecurityQuestion.java


package com.yulikexuan.ssl.domain.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Data
@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class SecurityQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(unique = true, nullable = false)
    private User user;

    @OneToOne(targetEntity = SecurityQuestionDefinition.class,
            fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private SecurityQuestionDefinition questionDefinition;

    private String answer;

}///:~