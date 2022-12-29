package org.fightjc.xybot.service.impl;

import org.fightjc.xybot.dao.UserRepository;
import org.fightjc.xybot.model.entity.User;
import org.fightjc.xybot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User '" + username + "' not found");
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password(user.getPassword())
                .authorities(Collections.emptyList())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

    public void createUser(User user) throws Exception {
        User exitedUser = userRepository.findByUsername(user.getUsername());
        if (exitedUser != null) {
            throw new Exception("user " + exitedUser.getUsername() + " has been created");
        }
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.createUser(user);
    }

    public User login(String username, String password) throws Exception {
        User exitedUser = userRepository.findByUsername(username);
        if (exitedUser == null) {
            throw new Exception("user invalid"); // TODO: do not expose detail
        }
        boolean result = passwordEncoder.matches(password, exitedUser.getPassword());
        if (!result) {
            throw new Exception("password error");
        }
        return exitedUser;
    }
}
