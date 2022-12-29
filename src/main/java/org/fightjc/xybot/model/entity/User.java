package org.fightjc.xybot.model.entity;

import lombok.Data;

/**
 * 映射表 User
 */
@Data
public class User {

    private Integer id;

    private String username;

    private String password;

    private boolean isDelete;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.isDelete = false;
    }
}
