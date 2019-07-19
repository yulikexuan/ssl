//: com.yulikexuan.ssl.domain.services.SecurityQuestionDefinitionService.java


package com.yulikexuan.ssl.domain.services;


import com.yulikexuan.ssl.domain.model.SecurityQuestionDefinition;
import com.yulikexuan.ssl.domain.repositories.ISecurityQuestionDefinitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class SecurityQuestionDefinitionService
        implements ISecurityQuestionDefinitionService {

    private final ISecurityQuestionDefinitionRepository secuQuestionDefRepository;

    @Autowired
    public SecurityQuestionDefinitionService(
            ISecurityQuestionDefinitionRepository secuQuestionDefRepository) {

        this.secuQuestionDefRepository = secuQuestionDefRepository;
    }

    @Override
    public Optional<SecurityQuestionDefinition> findById(Long id) {
        return this.secuQuestionDefRepository.findById(id);
    }

    @Override
    public List<SecurityQuestionDefinition> findAll() {
        return this.secuQuestionDefRepository.findAll();
    }

}///:~