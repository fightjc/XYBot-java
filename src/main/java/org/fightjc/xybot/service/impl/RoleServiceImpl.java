package org.fightjc.xybot.service.impl;

import org.fightjc.xybot.dao.RoleRepository;
import org.fightjc.xybot.model.dto.role.RoleDto;
import org.fightjc.xybot.model.entity.Role;
import org.fightjc.xybot.service.RoleService;
import org.fightjc.xybot.util.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public List<RoleDto> getRoles() {
        List<Role> roleList = roleRepository.getRoles();
        return ObjectMapper.mapAll(roleList, RoleDto.class);
    }

    @Override
    public RoleDto getRole(String roleId) {
        Role role = roleRepository.getRole(roleId);
        return ObjectMapper.map(role, RoleDto.class);
    }

    @Override
    public RoleDto getRoleByUserId(String userId) {
        Role role = roleRepository.getRoleByUserId(userId);
        return ObjectMapper.map(role, RoleDto.class);
    }
}
