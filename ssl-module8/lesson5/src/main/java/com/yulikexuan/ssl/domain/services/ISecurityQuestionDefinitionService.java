//: com.yulikexuan.ssl.domain.services.ISecurityQuestionDefinitionService.java


package com.yulikexuan.ssl.domain.services;


import com.yulikexuan.ssl.domain.model.SecurityQuestionDefinition;

import java.util.List;
import java.util.Optional;


public interface ISecurityQuestionDefinitionService {

    Optional<SecurityQuestionDefinition> findById(Long id);
    List<SecurityQuestionDefinition> findAll();

}///:~