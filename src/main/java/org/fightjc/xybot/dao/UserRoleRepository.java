package org.fightjc.xybot.dao;

import org.fightjc.xybot.model.entity.UserRole;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository {

    void createUserRole(UserRole userRole);

    void deleteUserRole(String userId);
}
