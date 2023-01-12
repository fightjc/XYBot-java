package org.fightjc.xybot.controller.api;

import org.apache.commons.lang3.StringUtils;
import org.fightjc.xybot.enums.ResultCode;
import org.fightjc.xybot.model.dto.ResultOutput;
import org.fightjc.xybot.model.dto.common.PageResultDto;
import org.fightjc.xybot.model.dto.user.*;
import org.fightjc.xybot.service.UserService;
import org.fightjc.xybot.util.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/all")
    public ResultOutput<PageResultDto<List<UserListDto>>> getAll(GetAllUserInput input) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // get user info list
        PageResultDto<List<UserDto>> pageResultDto = userService.getUsers(input);

        List<UserListDto> userList = pageResultDto.getItems()
                .stream()
                .map(user -> {
                    UserListDto listDto = ObjectMapper.map(user, UserListDto.class);
                    // user whether can be deleted
                    boolean deletable = !StringUtils.equals(user.getUsername(), username) && !"xybot".equals(username);
                    listDto.setDeletable(deletable);

                    return listDto;
                }) // do not expose password to front end
                .collect(Collectors.toList());

        return new ResultOutput<>(ResultCode.SUCCESS, new PageResultDto<>(pageResultDto.getTotalCount(), userList));
    }

    @GetMapping("/{userId}")
    public ResultOutput<UserListDto> get(@PathVariable String userId) {
        UserListDto dto = ObjectMapper.map(userService.getUser(userId), UserListDto.class);
        return new ResultOutput<>(ResultCode.SUCCESS, dto);
    }

    @PostMapping("/createOrUpdate")
    public ResultOutput<String> createOrUpdate(@Valid @RequestBody CreateOrUpdateUserInput input) {
        UserDto dto = ObjectMapper.map(input, UserDto.class);

        if (StringUtils.isEmpty(dto.getId())) {
            return userService.createUser(dto, input.getRoleId());
        } else {
            return userService.updateUser(dto, input.getRoleId());
        }
    }

    @PostMapping("/delete")
    public ResultOutput<String> delete(String userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
            String auth = grantedAuthority.getAuthority();
            if ("admin".equals(auth)) {
                return userService.deleteUser(userId);
            }
        }
        return new ResultOutput<>(ResultCode.UNAUTHORIZED, "该用户没有权限删除");
    }

}
