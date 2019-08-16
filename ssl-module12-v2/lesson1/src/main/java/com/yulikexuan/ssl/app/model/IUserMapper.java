//: com.yulikexuan.ssl.app.mapper.IUserMapper.java


package com.yulikexuan.ssl.app.model;


import com.yulikexuan.ssl.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;


@Component
@Mapper(uses = DateMapper.class, componentModel = "default")
public interface IUserMapper {

    IUserMapper INSTANCE = Mappers.getMapper(IUserMapper.class);

    @Mapping(target = "passwordConfirmation", ignore = true)
    UserDto userToUserDto(User user);

    User userDtoToUser(UserDto userDto);

}///:~