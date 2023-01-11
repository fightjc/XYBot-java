package org.fightjc.xybot.controller.api;

import org.fightjc.xybot.enums.ResultCode;
import org.fightjc.xybot.model.dto.ResultOutput;
import org.fightjc.xybot.model.dto.common.PageResultDto;
import org.fightjc.xybot.model.dto.user.*;
import org.fightjc.xybot.model.entity.Role;
import org.fightjc.xybot.security.JwtProvider;
import org.fightjc.xybot.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final static ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public LoginOutput login(@Valid @RequestBody LoginInput input) {
        //TODO: 登录重复判断
        String username = input.getUsername();
        String password = input.getPassword();
        UserDto user = userService.userLogin(username, password);
        if (user != null) {
            String token = jwtProvider.createToken(username);
            Role role = userService.getRoleByUserId(user.getId());
            return new LoginOutput(username, role.getName(), token);
        } else {
            return null;
        }
    }

    /**
     * 返回类型必须定义为 List<Object>，否则 org.fightjc.xybot.config.ResponseControllerAdvice 中返回
     * body 转化 json 失败报 HttpMessageNotWritableException
     */
    @GetMapping("/permissions")
    public List<Object> permissions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        //TODO: get permissions by role name from database
        String[] list = { "system.user", "system.role" };

        return Arrays.asList(list);
    }

    @PostMapping("/register")
    public ResultOutput<String> register(@Valid @RequestBody RegisterInput input) {
        UserDto newUser = modelMapper.map(input, UserDto.class);
        return userService.createUser(newUser);
    }

    @GetMapping("/all")
    public ResultOutput<PageResultDto<List<UserDto>>> getAll(GetAllUserInput input) {
        PageResultDto<List<UserDto>> pageResultDto = userService.getUsers(input);
        return new ResultOutput<>(ResultCode.SUCCESS, pageResultDto);
    }

    @PostMapping("/create")
    public ResultOutput<String> create(UserDto input) {
        return userService.createUser(input);
    }

    @PostMapping("/delete")
    public ResultOutput<String> delete(String username) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        for (GrantedAuthority grantedAuthority : authentication.getAuthorities()){
            if ("admin".equals(grantedAuthority.getAuthority())) {
                return userService.deleteUser(username);
            }
        }
        return new ResultOutput<>(ResultCode.UNAUTHORIZED, "该用户没有权限删除");
    }

}
