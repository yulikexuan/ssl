//: com.yulikexuan.ssl.domain.model.SecurityQuestionDefinition.java


package com.yulikexuan.ssl.domain.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;


@Data
@Entity
@NoArgsConstructor
@Builder @AllArgsConstructor
public class SecurityQuestionDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotEmpty
    private String text;

}///:~