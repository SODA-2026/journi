package com.soda2026.journi.user;

import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toDto(UserModel user){
        return new UserResponse(
          user.getName()
        );
    }
}
