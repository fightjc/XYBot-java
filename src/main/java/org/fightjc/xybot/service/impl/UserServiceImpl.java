package org.fightjc.xybot.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.fightjc.xybot.dao.RoleRepository;
import org.fightjc.xybot.dao.UserRepository;
import org.fightjc.xybot.dao.UserRoleRepository;
import org.fightjc.xybot.enums.ResultCode;
import org.fightjc.xybot.exception.ApiException;
import org.fightjc.xybot.model.dto.ResultOutput;
import org.fightjc.xybot.model.dto.common.PageResultDto;
import org.fightjc.xybot.model.dto.user.GetAllUserInput;
import org.fightjc.xybot.model.dto.user.UserDto;
import org.fightjc.xybot.model.dto.user.UserInfo;
import org.fightjc.xybot.model.entity.Role;
import org.fightjc.xybot.model.entity.User;
import org.fightjc.xybot.model.entity.UserRole;
import org.fightjc.xybot.service.UserService;
import org.fightjc.xybot.util.MessageUtil;
import org.fightjc.xybot.util.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ApiException("User '" + username + "' not found");
        }

        Role role = roleRepository.getRoleByUserId(user.getId());

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
    public UserDto userLogin(String username, String password) {
        User exitedUser = userRepository.findByUsername(username);
        if (exitedUser == null) {
            return null;
        }
        boolean result = passwordEncoder.matches(password, exitedUser.getPassword());
        if (!result) {
            return null;
        }
        return ObjectMapper.map(exitedUser, UserDto.class);
    }

    @Override
    public PageResultDto<List<UserDto>> getUsers(GetAllUserInput input) {
        int totalCount = userRepository.getUsersCount(input);
        List<UserDto> userList = userRepository.getUsers(input);

        return new PageResultDto<>(totalCount, userList);
    }

    @Override
    public UserDto getUser(String userId) {
        return userRepository.findById(userId);
    }

    @Override
    public UserInfo getUserInfo(String username) {
        User user = userRepository.findByUsername(username);
        UserDto userDto = getUser(user.getId());
        return ObjectMapper.map(userDto, UserInfo.class);
    }

    @Override
    public ResultOutput<String> createUser(UserDto userDto) {
        Role role = roleRepository.getDefaultRole();
        return createUser(userDto, role.getId());
    }

    @Override
    public ResultOutput<String> createUser(UserDto userDto, String roleId) {
        // whether user exists
        User exitedUser = userRepository.findByUsername(userDto.getUsername());
        if (exitedUser != null) {
            return new ResultOutput<>(ResultCode.FAILED, "user " + exitedUser.getUsername() + " has been created");
        }

        // whether role exists
        Role role = roleRepository.getRole(roleId);
        if (role == null) {
            return new ResultOutput<>(ResultCode.FAILED, "role " + roleId + " does not existed");
        }

        if (StringUtils.isEmpty(userDto.getPassword())) {
            return new ResultOutput<>(ResultCode.FAILED, "password can not be null");
        }

        User user = ObjectMapper.map(userDto, User.class);
        user.setId(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(user.getPassword())); // 加密密码
        user.setCreationTime(MessageUtil.getCurrentDateTime()); // 创建时间为当前
        userRepository.createUser(user);

        userRoleRepository.deleteUserRole(user.getId());
        userRoleRepository.createUserRole(new UserRole(user.getId(), roleId));

        return new ResultOutput<>(ResultCode.SUCCESS, ResultCode.SUCCESS.getMsg(), "创建成功");
    }

    @Override
    public ResultOutput<String> updateUser(UserDto userDto, String roleId) {
        // whether user exists
        User user = userRepository.findByUsername(userDto.getUsername());
        if (user == null) {
            return new ResultOutput<>(ResultCode.FAILED, "user " + userDto.getUsername() + " has been created");
        }

        // whether role exists
        Role role = roleRepository.getRole(roleId);
        if (role == null) {
            return new ResultOutput<>(ResultCode.FAILED, "role " + roleId + " does not existed");
        }

        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setActive(userDto.isActive());
        userRepository.updateUser(user);

        userRoleRepository.deleteUserRole(user.getId());
        userRoleRepository.createUserRole(new UserRole(user.getId(), roleId));

        return new ResultOutput<>(ResultCode.SUCCESS, ResultCode.SUCCESS.getMsg(), "更新成功");
    }

    @Override
    public ResultOutput<String> deleteUser(String userId) {
        UserDto user = userRepository.findById(userId);
        if (user == null) {
            return new ResultOutput<>(ResultCode.FAILED, ResultCode.FAILED.getMsg(), "找不到要删除的用户");
        }
        userRepository.deleteUser(userId);
        userRoleRepository.deleteUserRole(userId);
        return new ResultOutput<>(ResultCode.SUCCESS, ResultCode.SUCCESS.getMsg(), "删除成功");
    }

}
