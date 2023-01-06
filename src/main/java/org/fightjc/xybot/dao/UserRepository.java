package org.fightjc.xybot.dao;

import org.fightjc.xybot.model.entity.Role;
import org.fightjc.xybot.model.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository {

    /**
     * 获取用户
     * @param username
     * @return
     */
    User findByUsername(String username);

    /**
     * 新增一条用户数据
     * @param user
     */
    void createUser(User user);

    /**
     * 获取用户角色
     * @param username
     * @return
     */
    Role getRoleByUser(String username);
}
