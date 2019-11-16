//: com.yulikexuan.ssl.app.model.UserDto.java


package com.yulikexuan.ssl.app.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.yulikexuan.ssl.domain.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;


@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserDto {

    private Long id;

    @NotEmpty(message = "Username is required.")
    private String username;

    @NotEmpty(message = "Email is required.")
    private String email;

    private String phone;

    @NotEmpty(message = "Password is required.")
    private String password;

    private String secret;

    @NotEmpty(message = "Password confirmation is required.")
    private String passwordConfirmation;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ssZ", shape=JsonFormat.Shape.STRING)
    private OffsetDateTime created = null;

    private Boolean twoFactorAuthActivated;

    private Boolean enabled;

    private Set<RoleDto> roleDtos = new HashSet<>();

}///:~