package org.fightjc.xybot.pojo;

public class GroupSwitch {
    Integer id;

    Long groupId;

    String name;

    boolean isOn;

    public GroupSwitch(Long groupId, String name, boolean isOn) {
        this.groupId = groupId;
        this.name = name;
        this.isOn = isOn;
    }
}
