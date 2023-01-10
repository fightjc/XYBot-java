package org.fightjc.xybot.service;

import org.fightjc.xybot.model.dto.ResultOutput;
import org.fightjc.xybot.model.dto.user.UserDto;
import org.fightjc.xybot.model.entity.Role;

public interface UserService {

    ResultOutput<String> createUser(UserDto user);

    UserDto userLogin(String username, String password);

    Role getRoleByUsername(String username);

    ResultOutput<String> deleteUser(String username);
}
