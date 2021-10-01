package org.fightjc.xybot.pojo.bilibili;

import org.fightjc.xybot.util.MessageUtil;

/**
 * 映射表 bili_subscribeRecord
 */
public class SubscribeRecordBean {
    private Integer id;

    private Long groupId;

    private String mid;

    private boolean isActive;

    private Long modifiedUserId;

    private String modifiedTime;

    public SubscribeRecordBean(Long groupId, String mid, boolean isActive, Long modifiedUserId, String modifiedTime) {
        this.groupId = groupId;
        this.mid = mid;
        this.isActive = isActive;
        this.modifiedUserId = modifiedUserId;
        this.modifiedTime = modifiedTime;
    }

    public SubscribeRecordBean(SubscribeBean subscribeBean, Long modifiedUserId) {
        this.groupId = subscribeBean.getGroupId();
        this.mid = subscribeBean.getMid();
        this.isActive = subscribeBean.isActive();

        this.modifiedUserId = modifiedUserId;
        this.modifiedTime = MessageUtil.getCurrentDateTime();
    }
}
