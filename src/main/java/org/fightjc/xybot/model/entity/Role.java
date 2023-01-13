package org.fightjc.xybot.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 映射表 Role
 */
@Data
@AllArgsConstructor
public class Role {

    private String id;

    private String name;

    private String remark;

    private boolean isDefault;
}
