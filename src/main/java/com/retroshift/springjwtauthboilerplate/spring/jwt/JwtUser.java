package com.retroshift.springjwtauthboilerplate.spring.jwt;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class JwtUser implements UserDetails {

    private final long id;
    private final String email;
    private final String username;
    private final String password;
    private final Date lastResetDate;

    public JwtUser(long id, String email,
                   String username,
                   String password,
                   boolean enabled,
                   Date lastResetDate,
                   boolean notLocked) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.lastResetDate = lastResetDate;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public Date getLastResetDate() {
        return lastResetDate;
    }
}
