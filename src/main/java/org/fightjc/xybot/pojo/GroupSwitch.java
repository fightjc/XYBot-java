package org.fightjc.xybot.pojo;

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

    public Long getGroupId() {
        return groupId;
    }

    public String getName() {
        return name;
    }

    public boolean isOn() {
        return isOn;
    }
}
