package org.fightjc.xybot.model.dto.user;

import lombok.Data;

@Data
public class UserDto {

    private String id;

    private String username;

    private String password;

    private String name;

    private String email;

    private String creationTime;

    private boolean isActive;

    private String deletionTime;

    private String roleName;
}
