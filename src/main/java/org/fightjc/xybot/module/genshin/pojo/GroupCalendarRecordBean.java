package org.fightjc.xybot.module.genshin.pojo;

public class GroupCalendarRecordBean {
    private Integer id;

    private Long groupId;

    private boolean isActive;

    private Long modifiedUserId;

    private String modifiedTime;

    public GroupCalendarRecordBean(Long groupId, boolean isActive, Long modifiedUserId, String modifiedTime) {
        this.groupId = groupId;
        this.isActive = isActive;
        this.modifiedUserId = modifiedUserId;
        this.modifiedTime = modifiedTime;
    }
}
