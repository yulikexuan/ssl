//: com.yulikexuan.ssl.m1.lesson6.app.v1.mapper.IUserMapper.java


package com.yulikexuan.ssl.m1.lesson6.app.v1.mapper;


import com.yulikexuan.ssl.m1.lesson6.app.v1.model.UserDto;
import com.yulikexuan.ssl.m1.lesson6.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;


@Component
@Mapper(uses = DateMapper.class, componentModel = "default")
public interface IUserMapper {

    IUserMapper INSTANCE = Mappers.getMapper(IUserMapper.class);

    UserDto userToUserDto(User user);

    User userDtoToUser(UserDto userDto);

}///:~