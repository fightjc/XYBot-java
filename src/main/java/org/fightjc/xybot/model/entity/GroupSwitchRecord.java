package org.fightjc.xybot.model.entity;

import lombok.Data;
import org.fightjc.xybot.util.MessageUtil;

/**
 * 映射表 GroupSwitchRecord
 */
@Data
public class GroupSwitchRecord {
    private Integer id;

    private Long groupId;

    private String name;

    private boolean isOn;

    private Long modifiedUserId;

    private String modifiedTime;

    public GroupSwitchRecord(Long groupId, String name, boolean isOn, Long modifiedUserId, String modifiedTime) {
        this.groupId = groupId;
        this.name = name;
        this.isOn = isOn;
        this.modifiedUserId = modifiedUserId;
        this.modifiedTime = modifiedTime;
    }

    public GroupSwitchRecord(GroupSwitch groupSwitch, Long modifiedUserId) {
        this.groupId = groupSwitch.getGroupId();
        this.name = groupSwitch.getName();
        this.isOn = groupSwitch.isOn();

        this.modifiedUserId = modifiedUserId;
        this.modifiedTime = MessageUtil.getCurrentDateTime();
    }
}
