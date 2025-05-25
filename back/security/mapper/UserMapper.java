package com.laba.products.security.mapper;

import com.laba.products.app.entity.User;
import com.laba.products.security.dto.UserGetDto;
import com.laba.products.security.dto.UserRegistrationDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface UserMapper {

    UserGetDto userToUserGetDto(User user);

    User userGetDtoToUser(UserGetDto userGetDto);

    User userRegistrationDtoToUser(UserRegistrationDto userRegistrationDto);

}
