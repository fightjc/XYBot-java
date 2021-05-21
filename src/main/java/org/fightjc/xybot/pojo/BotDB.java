package org.fightjc.xybot.pojo;

public class BotDB {
    Integer id;

    String remark;

    int version;

    String executeTime;

    public BotDB(String remark, int version, String executeTime) {
        this.remark = remark;
        this.version = version;
        this.executeTime = executeTime;
    }

    public int getVersion() {
        return version;
    }
}
