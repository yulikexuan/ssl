//: com.yulikexuan.ssl.domain.model.VerificationToken.java


package com.yulikexuan.ssl.domain.model;


import com.yulikexuan.ssl.domain.services.IUserService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;


@Data
@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class VerificationToken implements IUserToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @OneToOne
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    private Date expiryDate;

}///:~