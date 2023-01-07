package org.fightjc.xybot.model.dto.user;

import lombok.Data;

@Data
public class UserDto {

    private Integer id;

    private String username;

    private String password;

    private String name;

    private String email;

    private String creationTime;

    private boolean isDelete;

    private String deletionTime;
}
