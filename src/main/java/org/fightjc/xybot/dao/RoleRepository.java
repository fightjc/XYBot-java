package org.fightjc.xybot.dao;

import org.fightjc.xybot.model.entity.Role;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository {

    List<Role> getRoles();

    Role getRole(String roleId);

    Role getDefaultRole();

    /**
     * 获取用户角色
     * @param userId
     * @return
     */
    Role getRoleByUserId(String userId);
}
