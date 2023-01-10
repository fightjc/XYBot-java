package org.fightjc.xybot.service.impl;

import org.fightjc.xybot.dao.UserRepository;
import org.fightjc.xybot.enums.ResultCode;
import org.fightjc.xybot.exception.ApiException;
import org.fightjc.xybot.model.dto.ResultOutput;
import org.fightjc.xybot.model.dto.user.UserDto;
import org.fightjc.xybot.model.entity.Role;
import org.fightjc.xybot.model.entity.User;
import org.fightjc.xybot.service.UserService;
import org.fightjc.xybot.util.MessageUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final static ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ApiException("User '" + username + "' not found");
        }

        Role role = userRepository.getRoleByUser(user.getUsername());

        return org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password(user.getPassword())
                .authorities(role.getName())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

    @Override
    public ResultOutput<String> createUser(UserDto newUser) {
        User exitedUser = userRepository.findByUsername(newUser.getUsername());
        if (exitedUser != null) {
            return new ResultOutput<>(ResultCode.FAILED, "user " + exitedUser.getUsername() + " has been created");
        }
        User user = modelMapper.map(newUser, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword())); // 加密密码
        user.setCreationTime(MessageUtil.getCurrentDateTime()); // 创建时间为当前
        userRepository.createUser(user);
        return new ResultOutput<>(ResultCode.SUCCESS);
    }

    @Override
    public UserDto userLogin(String username, String password) {
        User exitedUser = userRepository.findByUsername(username);
        if (exitedUser == null) {
            return null;
        }
        boolean result = passwordEncoder.matches(password, exitedUser.getPassword());
        if (!result) {
            return null;
        }
        return modelMapper.map(exitedUser, UserDto.class);
    }

    @Override
    public Role getRoleByUsername(String username) {
        return userRepository.getRoleByUser(username);
    }

    @Override
    public ResultOutput<String> deleteUser(String username) {
        return new ResultOutput<>(ResultCode.SUCCESS);
    }
}
