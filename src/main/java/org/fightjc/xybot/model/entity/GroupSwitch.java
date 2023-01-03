package org.fightjc.xybot.model.entity;

import lombok.Data;

/**
 * 映射表 GroupSwitch
 */
@Data
public class GroupSwitch {

    private Integer id;

    private Long groupId;

    private String name;

    private boolean isOn;

    public GroupSwitch(Long groupId, String name, boolean isOn) {
        this.groupId = groupId;
        this.name = name;
        this.isOn = isOn;
    }
}
