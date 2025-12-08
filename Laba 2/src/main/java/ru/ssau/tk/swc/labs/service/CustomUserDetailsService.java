package ru.ssau.tk.swc.labs.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.ssau.tk.swc.labs.entity.users;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsersService usersService;

    public CustomUserDetailsService(UsersService usersService) {
        this.usersService = usersService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        users user = usersService.findUserByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return User.builder()
                .username(user.getLogin())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }
}