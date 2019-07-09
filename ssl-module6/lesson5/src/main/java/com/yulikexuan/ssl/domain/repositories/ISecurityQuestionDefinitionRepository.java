//: com.yulikexuan.ssl.domain.repositories.SecurityQuestionDefinitionRepository.java


package com.yulikexuan.ssl.domain.repositories;


import com.yulikexuan.ssl.domain.model.SecurityQuestionDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ISecurityQuestionDefinitionRepository
        extends JpaRepository<SecurityQuestionDefinition, Long> {

}///:~