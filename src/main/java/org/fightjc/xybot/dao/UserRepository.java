package org.fightjc.xybot.dao;

import org.fightjc.xybot.model.dto.user.GetAllUserInput;
import org.fightjc.xybot.model.dto.user.UserDto;
import org.fightjc.xybot.model.entity.Role;
import org.fightjc.xybot.model.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository {

    /**
     * 获取用户列表
     */
    int getUsersCount(GetAllUserInput input);
    List<UserDto> getUsers(GetAllUserInput input);

    UserDto getUser(String userId);

    /**
     * 获取指定用户
     * @param username
     * @return
     */
    User findByUsername(String username);

    /**
     * 新增一条用户数据
     * @param user
     */
    void createUser(User user);

    void updateUser(User user);
}
