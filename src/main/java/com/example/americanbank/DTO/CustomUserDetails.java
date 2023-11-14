package com.example.americanbank.DTO;

import com.example.americanbank.Entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // If you want to add roles or authorities later, modify this method
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserName();
    }

    @Override
    public boolean isAccountNonExpired() {
        // Implement account expiry check here if required
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Implement account lock check here if required
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Implement credentials expiry check here if required
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Implement account enable/disable check here if required
        return true;
    }
}
