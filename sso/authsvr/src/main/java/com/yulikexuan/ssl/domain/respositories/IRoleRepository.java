//: com.yulikexuan.ssl.domain.respositories.IRoleRepository.java


package com.yulikexuan.ssl.domain.respositories;

import com.yulikexuan.ssl.domain.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface IRoleRepository extends JpaRepository<Role, Long> {

    List<Role> findByName(String roleName);

}///:~