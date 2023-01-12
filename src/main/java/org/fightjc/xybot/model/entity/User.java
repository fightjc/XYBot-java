package org.fightjc.xybot.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 映射表 User
 */
@Data
@AllArgsConstructor
public class User {

    private String id;

    private String username;

    private String password;

    private String name;

    private String email;

    private String creationTime;

    private boolean isActive;

    private String deletionTime;
}
