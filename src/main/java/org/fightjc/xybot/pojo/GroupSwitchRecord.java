package org.fightjc.xybot.pojo;

public class GroupSwitchRecord {
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
}
