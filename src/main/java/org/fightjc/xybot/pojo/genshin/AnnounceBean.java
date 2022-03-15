package org.fightjc.xybot.pojo.genshin;

import java.util.Date;

public class AnnounceBean {
    AnnounceType type;
    String title;
    Date startTime;
    Date endTime;
    boolean isForever;

    public enum AnnounceType {
        abyss, event, gacha, award, other
    }

    public AnnounceBean(AnnounceType type, String title, Date startTime, Date endTime, boolean isForever) {
        this.type = type;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isForever = isForever;
    }

    public AnnounceType getType() {
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
        return (endTime.getTime() - current) / (24 * 60 * 60 * 1000);
    }

    public String getDeadLineDescription() {
        long current = new Date().getTime();
        long start = (startTime.getTime() - current) / (24 * 60 * 60 * 1000);
        long deadline =  (endTime.getTime() - current) / (24 * 60 * 60 * 1000);

        String des = "";
        if (start > 0) {
            des = start + "天后开始";
        } else if (start == 0) {
            des = "即将开始";
        } else {
            if (deadline > 0) {
                if (isForever) {
                    des = "永久开放";
                } else {
                    des = deadline + "天后结束";
                }
            } else {
                des = "即将结束";
            }
        }

        return des;
    }
}
