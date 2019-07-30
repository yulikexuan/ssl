//: com.yulikexuan.ssl.domain.model.User.java


package com.yulikexuan.ssl.domain.model;


import com.google.common.collect.ImmutableSet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
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
    private Set<Role> roles;

    public Set<Role> getRoles() {
        return ImmutableSet.copyOf(this.roles);
    }

}///:~
