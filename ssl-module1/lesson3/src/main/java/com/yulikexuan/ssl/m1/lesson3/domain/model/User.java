//: com.yulikexuan.ssl.m1.lesson3.domain.model.User.java


package com.yulikexuan.ssl.m1.lesson3.domain.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.sql.Timestamp;


@Data
@Entity
@NoArgsConstructor
@Builder @AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Username is required.")
    private String username;

    @NotEmpty(message = "Email is required.")
    private String email;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp created;

}///:~
