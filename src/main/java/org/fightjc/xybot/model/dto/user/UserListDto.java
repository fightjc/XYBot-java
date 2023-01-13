package org.fightjc.xybot.model.dto.user;

import lombok.Data;

@Data
public class UserListDto {

    private String id;

    private String username;

    private String name;

    private String email;

    private String creationTime;

    private boolean active;

    private String deletionTime;

    private String roleName;

    private boolean deletable;
}
