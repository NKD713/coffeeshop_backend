package com.coffeeshop.mycoffee.mapper;

import com.coffeeshop.mycoffee.dto.userdto.request.UserCreationRequest;
import com.coffeeshop.mycoffee.dto.userdto.request.UserUpdateRequest;
import com.coffeeshop.mycoffee.dto.userdto.response.UserResponse;
import com.coffeeshop.mycoffee.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);

    UserResponse toUserResponse(User user);

    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
