package com.soda2026.journi.security;

import com.soda2026.journi.user.UserModel;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public class UserPrincipal extends User{

    private static final long serialVersionUID = 1L;
    private final Long id;
    public UserPrincipal(UserModel user){
        super(
                user.getEmail(),
                user.getPasswordHash(),
                List.of()
        );
        this.id = user.getId();
    }

    public Long getId() {
        return id;
    }
}