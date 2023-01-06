package org.fightjc.xybot.controller.api;

import org.fightjc.xybot.exception.ApiException;
import org.fightjc.xybot.model.dto.user.LoginInput;
import org.fightjc.xybot.model.dto.user.LoginOutput;
import org.fightjc.xybot.model.dto.user.RegisterInput;
import org.fightjc.xybot.model.dto.user.UserDto;
import org.fightjc.xybot.model.entity.Role;
import org.fightjc.xybot.security.JwtProvider;
import org.fightjc.xybot.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final static ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private UserService userService;

    @GetMapping("/test")
    public String test() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @PostMapping("/login")
    @ResponseBody
    public LoginOutput login(@Valid @RequestBody LoginInput input) {
        //TODO: 登陆重复判断
        String username = input.getUsername();
        String password = input.getPassword();
        UserDto user = userService.userLogin(username, password);
        if (user != null) {
            String token = jwtProvider.createToken(username);
            Role role = userService.getRoleByUsername(username);
            return new LoginOutput(username, role.getName(), token);
        } else {
            throw new ApiException("login fail");
        }
    }

    @GetMapping("/logout")
    public String logout() {
        //TODO:
        return "logout success";
    }

    @PostMapping("/register")
    @ResponseBody
    public String register(@Valid @RequestBody RegisterInput input) {
        UserDto newUser = modelMapper.map(input, UserDto.class);
        userService.createUser(newUser);
        return "create user " + input.getUsername() + " success!";
    }
}
