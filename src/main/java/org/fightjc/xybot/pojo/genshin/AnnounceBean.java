package org.fightjc.xybot.pojo.genshin;

import java.util.Date;

public class AnnounceBean {
    int type;
    String title;
    Date startTime;
    Date endTime;
    boolean isForever;

    public AnnounceBean(int type, String title, Date startTime, Date endTime, boolean isForever) {
        this.type = type;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isForever = isForever;
    }

    public int getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public Date getStartTime() {
        return startTime;
    }

    public long getDeadLine() {
        long current = new Date().getTime();
        return (endTime.getTime() - current) / 24 * 60 * 60 * 1000;
    }

    public boolean isForever() {
        return isForever;
    }
}
