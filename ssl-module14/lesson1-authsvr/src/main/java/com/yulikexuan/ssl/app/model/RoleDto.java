//: com.yulikexuan.ssl.app.model.RoleDto.java


package com.yulikexuan.ssl.app.model;


import com.yulikexuan.ssl.domain.model.Privilege;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class RoleDto {

    private Long id;
    private String name;

    Set<PrivilegeDto> privilegeDtos = new HashSet<>();;

}///:~