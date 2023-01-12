package org.fightjc.xybot.service;

import org.fightjc.xybot.model.dto.role.RoleDto;
import org.fightjc.xybot.model.entity.Role;

import java.util.List;

public interface RoleService {

    List<RoleDto> getRoles();

    RoleDto getRole(String roleId);

    RoleDto getRoleByUserId(String userId);
}
