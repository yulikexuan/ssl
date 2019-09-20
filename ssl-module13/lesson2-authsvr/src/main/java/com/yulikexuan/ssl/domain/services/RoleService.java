//: com.yulikexuan.ssl.domain.services.RoleService.java


package com.yulikexuan.ssl.domain.services;


import com.yulikexuan.ssl.domain.model.Role;
import com.yulikexuan.ssl.domain.respositories.IRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class RoleService implements IRoleService {

    private final IRoleRepository roleRepository;

    @Autowired
    public RoleService(IRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public long count() {
        return this.roleRepository.count();
    }

    @Override
    public List<Role> findAllRoles() {
        return this.roleRepository.findAll();
    }

    @Override
    public Optional<Role> saveRole(Role role) {
        return Optional.ofNullable(role)
                .map(r -> this.roleRepository.save(role));
    }

    @Override
    public Optional<Role> findByRoleName(String name) {
        return this.roleRepository.findByName(name).stream().findAny();
    }

}///:~