//: com.yulikexuan.ssl.domain.model.Privilege.java


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
public class Privilege {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    /*
     * Role is the owner of this many-to-many relationship
     * mappedBy attribute's value is the field name on the owner side in Role
     */
    @ManyToMany(mappedBy = "privileges")
    private Set<Role> roles;

    public Set<Role> getRoles() {
        return ImmutableSet.copyOf(this.roles);
    }

}///:~