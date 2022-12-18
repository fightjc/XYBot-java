package org.fightjc.xybot.module.bilibili.pojo;

/**
 * 映射表 bili_subscribe
 */
public class SubscribeBean {
    private Integer id;

    private Long groupId;

    private String mid;

    private boolean isActive;

    public SubscribeBean(Long groupId, String mid, boolean isActive) {
        this.groupId = groupId;
        this.mid = mid;
        this.isActive = isActive;
    }

    public Long getGroupId() {
        return groupId;
    }

    public String getMid() {
        return mid;
    }

    public boolean isActive() {
        return isActive;
    }
}
