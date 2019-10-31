//: com.yulikexuan.ssl.domain.services.IPrivilegeService.java


package com.yulikexuan.ssl.domain.services;


import com.yulikexuan.ssl.domain.model.Privilege;

import java.util.List;
import java.util.Optional;

public interface IPrivilegeService {

    long count();

    List<Privilege> findAllPrivileges();

    Optional<Privilege> savePrivilege(Privilege privilege);

    Optional<Privilege> findByPrivilegeName(String name);

}///:~