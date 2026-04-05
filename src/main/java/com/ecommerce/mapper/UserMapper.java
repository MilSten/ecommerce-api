// mapper/UserMapper.java
package com.ecommerce.mapper;

import com.ecommerce.dto.user.UserDto;
import com.ecommerce.entity.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    User toEntity(UserDto userDto);
}