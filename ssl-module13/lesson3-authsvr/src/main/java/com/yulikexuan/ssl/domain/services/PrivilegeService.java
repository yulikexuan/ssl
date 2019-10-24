//: com.yulikexuan.ssl.domain.services.PrivilegeService.java


package com.yulikexuan.ssl.domain.services;


import com.yulikexuan.ssl.domain.model.Privilege;
import com.yulikexuan.ssl.domain.respositories.IPrivilegeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class PrivilegeService implements IPrivilegeService {

    private final IPrivilegeRepository privilegeRepository;

    @Autowired
    public PrivilegeService(IPrivilegeRepository privilegeRepository) {
        this.privilegeRepository = privilegeRepository;
    }

    @Override
    public long count() {
        return this.privilegeRepository.count();
    }

    @Override
    public List<Privilege> findAllPrivileges() {
        return this.privilegeRepository.findAll();
    }

    @Override
    public Optional<Privilege> savePrivilege(Privilege privilege) {
        return Optional.ofNullable(privilege)
                .map(p -> this.privilegeRepository.save(p));
    }

    @Override
    public Optional<Privilege> findByPrivilegeName(String name) {
        return this.privilegeRepository.findByName(name).stream().findAny();
    }
}///:~