package org.fightjc.xybot.controller.api;

import org.fightjc.xybot.enums.ResultCode;
import org.fightjc.xybot.model.dto.ResultOutput;
import org.fightjc.xybot.model.dto.auth.LoginInput;
import org.fightjc.xybot.model.dto.auth.LoginOutput;
import org.fightjc.xybot.model.dto.auth.RegisterInput;
import org.fightjc.xybot.model.dto.role.RoleDto;
import org.fightjc.xybot.model.dto.user.UserDto;
import org.fightjc.xybot.model.dto.user.UserInfo;
import org.fightjc.xybot.model.entity.Role;
import org.fightjc.xybot.security.JwtProvider;
import org.fightjc.xybot.service.RoleService;
import org.fightjc.xybot.service.UserService;
import org.fightjc.xybot.util.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;

    @PostMapping("/login")
    public LoginOutput login(@Valid @RequestBody LoginInput input) {
        //TODO: 登录重复判断
        String username = input.getUsername();
        String password = input.getPassword();
        UserDto user = userService.userLogin(username, password);
        if (user != null) {
            String token = jwtProvider.createToken(user.getUsername());
            RoleDto role = roleService.getRoleByUserId(user.getId());
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

    @GetMapping("/user")
    public ResultOutput<UserInfo> user() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        UserInfo info = userService.getUserInfo(username);

        return new ResultOutput<>(ResultCode.SUCCESS, info);
    }

    @PostMapping("/register")
    public ResultOutput<String> register(@Valid @RequestBody RegisterInput input) {
        UserDto newUser = ObjectMapper.map(input, UserDto.class);
        return userService.createUser(newUser);
    }

    @PostMapping("/logout")
    public ResultOutput<String> logout() {
        return new ResultOutput<>(ResultCode.SUCCESS);
    }
}
