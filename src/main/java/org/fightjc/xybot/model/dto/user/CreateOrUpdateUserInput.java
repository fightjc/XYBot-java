package org.fightjc.xybot.model.dto.user;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class CreateOrUpdateUserInput {

    private String id;

    @NotNull(message = "用户名不能为空")
    @Size(min = 1, max = 100)
    private String username;

    @Size(min = 1, max = 100)
    private String password;

    @NotNull(message = "名称不能为空")
    @Size(min = 1, max = 100)
    private String name;

    @Email
    @Size(max = 100)
    private String email;

    @NotNull(message = "角色不能为空")
    private String roleId;

    private boolean active;
}
