package org.fightjc.xybot.pojo.genshin;

/**
 * 映射表 genshin_calendar
 */
public class GroupCalendarBean {
    private Integer id;

    private Long groupId;

    private boolean isActive;

    public GroupCalendarBean(Long groupId, boolean isActive) {
        this.groupId = groupId;
        this.isActive = isActive;
    }

    public Long getGroupId() {
        return groupId;
    }

    public boolean isActive() {
        return isActive;
    }
}
