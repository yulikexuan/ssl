//: com.yulikexuan.ssl.app.mapper.IUserListMapper.java


package com.yulikexuan.ssl.app.model;


import com.yulikexuan.ssl.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Component
@Mapper(componentModel = "spring")
public interface IUserListMapper {

    IUserListMapper INSTANCE = Mappers.getMapper(IUserListMapper.class);

    default UserListDto userListToUserListDto(List<User> users) {

        List<UserDto> dtos = Optional.ofNullable(users)
                .orElse(List.of())
                .stream()
                .map(IUserMapper.INSTANCE::userToUserDto)
                .collect(Collectors.toList());

        return UserListDto.builder().users(dtos).build();
    }

}///:~