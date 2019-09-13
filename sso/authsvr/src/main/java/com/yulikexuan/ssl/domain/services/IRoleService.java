//: com.yulikexuan.ssl.domain.services.IRoleService.java


package com.yulikexuan.ssl.domain.services;


import com.yulikexuan.ssl.domain.model.Role;

import java.util.List;
import java.util.Optional;


public interface IRoleService {

    long count();

    List<Role> findAllRoles();

    Optional<Role> saveRole(Role role);

    Optional<Role> findByRoleName(String name);

}///:~