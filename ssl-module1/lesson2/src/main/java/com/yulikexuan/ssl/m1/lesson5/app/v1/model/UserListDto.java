//: com.yulikexuan.ssl.m1.lesson5.app.v1.model.UserListDto.java


package com.yulikexuan.ssl.m1.lesson5.app.v1.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@Builder
public class UserListDto {

    private final List<UserDto> users;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public UserListDto(@JsonProperty("users") List<UserDto> users) {
        this.users = users;
    }

}///:~