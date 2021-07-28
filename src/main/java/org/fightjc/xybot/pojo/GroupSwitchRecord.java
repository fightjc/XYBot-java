package org.fightjc.xybot.pojo;

import org.fightjc.xybot.util.MessageUtil;

public class GroupSwitchRecord {
    Integer id;

    Long groupId;

    String name;

    boolean isOn;

    Long modifiedUserId;

    String modifiedTime;

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
