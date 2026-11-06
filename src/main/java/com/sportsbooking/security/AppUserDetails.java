package com.sportsbooking.security;

import com.sportsbooking.enums.Role;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.Serial;
import java.util.List;

public class AppUserDetails extends org.springframework.security.core.userdetails.User {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Long id;
    private final String fullName;
    private final Role role;

    public AppUserDetails(com.sportsbooking.entity.User user) {
        super(
            user.getUsername(),
            user.getPasswordHash(),
            user.isEnabled(),
            true,
            true,
            true,
            List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
        this.id = user.getId();
        this.fullName = user.getFullName();
        this.role = user.getRole();
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public Role getRole() {
        return role;
    }
}
