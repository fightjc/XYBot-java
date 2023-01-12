package org.fightjc.xybot.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 映射表 UserRole
 */
@Data
@AllArgsConstructor
public class UserRole {

    private String userId;

    private String roleId;
}
