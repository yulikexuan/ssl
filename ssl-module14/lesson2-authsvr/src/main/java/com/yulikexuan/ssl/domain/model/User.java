//: com.yulikexuan.ssl.domain.model.User.java


package com.yulikexuan.ssl.domain.model;


import com.google.common.collect.ImmutableSet;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


@Data
@Entity
@NoArgsConstructor
@Builder @AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    @Column
    private String phone;

    @Column
    private String secret;

    @Column
    private String password;

    @Column
    private Boolean enabled;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp created;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id",
                    referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id",
                    referencedColumnName = "id"))
    private Set<Role> roles = new HashSet<>();

    public Set<Role> getRoles() {
        return Optional.ofNullable(this.roles)
                .map(r -> ImmutableSet.copyOf(r))
                .orElse(ImmutableSet.of());
    }

}///:~
