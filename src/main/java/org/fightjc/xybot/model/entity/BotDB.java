package org.fightjc.xybot.model.entity;

import lombok.Data;

/**
 * 映射表 __migrationhistory
 */
@Data
public class BotDB {

    private Integer id;

    private String remark;

    private int version;

    private String executeTime;

    public BotDB(String remark, int version, String executeTime) {
        this.remark = remark;
        this.version = version;
        this.executeTime = executeTime;
    }
}
