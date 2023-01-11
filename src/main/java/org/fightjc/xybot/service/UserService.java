package org.fightjc.xybot.service;

import org.fightjc.xybot.model.dto.ResultOutput;
import org.fightjc.xybot.model.dto.common.PageResultDto;
import org.fightjc.xybot.model.dto.user.GetAllUserInput;
import org.fightjc.xybot.model.dto.user.UserDto;
import org.fightjc.xybot.model.entity.Role;

import java.util.List;

public interface UserService {

    PageResultDto<List<UserDto>> getUsers(GetAllUserInput input);

    ResultOutput<String> createUser(UserDto user);

    UserDto userLogin(String username, String password);

    Role getRoleByUserId(Integer userId);

    ResultOutput<String> deleteUser(String username);
}
