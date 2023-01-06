package org.fightjc.xybot.model.dto.user;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RegisterInput {

    @NotNull(message = "用户名不能为空")
    @Size(min = 1, max = 100)
    private String username;

    @NotNull(message = "密码不能为空")
    @Size(min = 1, max = 100)
    private String password;

    @NotNull(message = "名称不能为空")
    @Size(min = 1, max = 100)
    private String name;

    @Size(max = 100)
    private String email;
}
