//: com.yulikexuan.ssl.app.model.IPrivilegeMapper.java


package com.yulikexuan.ssl.app.model;


import com.yulikexuan.ssl.domain.model.Privilege;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;


@Component
@Mapper(componentModel = "default")
public interface IPrivilegeMapper {

    IPrivilegeMapper INSTANCE = Mappers.getMapper(IPrivilegeMapper.class);

    PrivilegeDto privilegeToPrivilegeDto(Privilege privilege);
    Privilege privilegeDtoToPrivilege(PrivilegeDto privilegeDto);

}///:~