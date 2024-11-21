package com.consultas.mappers;

import com.consultas.dto.SignUpDto;
import com.consultas.dto.UserDto;
import com.consultas.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toUserDto(User user);

    @Mapping(target = "password", ignore = true)
    User signUpToUser(SignUpDto signUpDto);

}
