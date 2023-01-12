package org.fightjc.xybot.controller.api;

import org.fightjc.xybot.enums.ResultCode;
import org.fightjc.xybot.model.dto.ResultOutput;
import org.fightjc.xybot.model.dto.role.RoleDto;
import org.fightjc.xybot.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/all")
    public ResultOutput<List<RoleDto>> getAll() {
        List<RoleDto> roleList = roleService.getRoles();
        return new ResultOutput<>(ResultCode.SUCCESS, roleList);
    }
}
