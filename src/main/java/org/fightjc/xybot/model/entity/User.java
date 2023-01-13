package org.fightjc.xybot.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 映射表 User
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private String id;

    private String username;

    private String password;

    private String name;

    private String email;

    private String creationTime;

    private boolean active;

    private String deletionTime;
}
