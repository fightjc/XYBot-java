package org.fightjc.xybot.controller;

import org.fightjc.xybot.exception.ApiException;
import org.fightjc.xybot.model.dto.user.LoginInput;
import org.fightjc.xybot.model.dto.user.LoginOutput;
import org.fightjc.xybot.model.entity.User;
import org.fightjc.xybot.security.JwtProvider;
import org.fightjc.xybot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {

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
        try {
            User user = userService.login(input.getUsername(), input.getPassword());
            if (user != null) {
                String token = jwtProvider.createToken(user.getUsername());
                return new LoginOutput(input.getUsername(), token);
            } else {
                throw new ApiException("login fail");
            }
        } catch (Exception e) {
            throw new ApiException(e.getMessage());
        }
    }

    @PostMapping("/register")
    public String register(@Valid @RequestBody LoginInput input) {
        User user = new User(input.getUsername(), input.getPassword());
        try {
            userService.createUser(user);
            return "create user " + input.getUsername() + " success!";
        } catch (Exception e) {
            throw new ApiException(e.getMessage());
        }
    }
}
