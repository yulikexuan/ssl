//: com.yulikexuan.ssl.domain.model.Role.java


package com.yulikexuan.ssl.domain.model;

import com.google.common.collect.ImmutableSet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;


@Data
@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    /*
     * This class is picked up for being the owner of many-to-many relationship
     * joinColumns connect to the owner side of the relationship
     * inverseJoinColumns connect to the target side, which is Privilege
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "roles_privileges",
            joinColumns = @JoinColumn(name = "role_id",
                    referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "privilege_id",
                    referencedColumnName = "id"))
    private Set<Privilege> privileges;

    public Set<Privilege> getPrivileges() {
        return ImmutableSet.copyOf(this.privileges);
    }

}///:~