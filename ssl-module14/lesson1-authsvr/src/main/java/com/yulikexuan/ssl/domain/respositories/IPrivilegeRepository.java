//: com.yulikexuan.ssl.domain.respositories.IPrivilegeRepository.java


package com.yulikexuan.ssl.domain.respositories;


import com.yulikexuan.ssl.domain.model.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface IPrivilegeRepository extends JpaRepository<Privilege, Long> {

    List<Privilege> findByName(String name);

}///:~