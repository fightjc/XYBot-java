package org.fightjc.xybot.service;

import org.fightjc.xybot.model.dto.user.UserDto;
import org.fightjc.xybot.model.entity.Role;
import org.fightjc.xybot.model.entity.User;

public interface UserService {

    void createUser(UserDto user);

    UserDto userLogin(String username, String password);

    Role getRoleByUsername(String username);
}
