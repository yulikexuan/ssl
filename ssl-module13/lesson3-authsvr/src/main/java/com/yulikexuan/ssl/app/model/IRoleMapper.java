//: com.yulikexuan.ssl.app.model.IRoleMapper.java


package com.yulikexuan.ssl.app.model;


import com.yulikexuan.ssl.domain.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;


@Component
@Mapper(uses = IPrivilegeMapper.class, componentModel = "default")
public interface IRoleMapper {

    @Mapping(source = "privileges", target = "privilegeDtos")
    RoleDto roleToRoleDto(Role role);

    @Mapping(source = "privilegeDtos", target = "privileges")
    Role roleDtoToRole(RoleDto roleDto);

}///:~