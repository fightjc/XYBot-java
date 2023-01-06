package org.fightjc.xybot.model.entity;

import lombok.Data;

/**
 * 映射表 Role
 */
@Data
public class Role {

    private Integer id;

    private String name;

    private boolean isDefault;
}
