package org.fightjc.xybot.service;

import org.fightjc.xybot.model.dto.ResultOutput;
import org.fightjc.xybot.model.dto.common.PageResultDto;
import org.fightjc.xybot.model.dto.user.GetAllUserInput;
import org.fightjc.xybot.model.dto.user.UserDto;

import java.util.List;

public interface UserService {

    UserDto userLogin(String username, String password);

    PageResultDto<List<UserDto>> getUsers(GetAllUserInput input);

    UserDto getUser(String userId);

    ResultOutput<String> createUser(UserDto user);

    ResultOutput<String> createUser(UserDto userDto, String roleId);

    ResultOutput<String> updateUser(UserDto userDto, String roleId);

    ResultOutput<String> deleteUser(String userId);
}
