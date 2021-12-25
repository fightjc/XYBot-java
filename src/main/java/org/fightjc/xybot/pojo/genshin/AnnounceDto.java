package org.fightjc.xybot.pojo.genshin;

import java.util.List;

public class AnnounceDto {
    List<AnnounceBean> abyss;
    List<AnnounceBean> gacha;
    List<AnnounceBean> event;

    public AnnounceDto(List<AnnounceBean> abyss, List<AnnounceBean> gacha, List<AnnounceBean> event) {
        this.abyss = abyss;
        this.gacha = gacha;
        this.event = event;
    }

    public List<AnnounceBean> getAbyss() {
        return abyss;
    }

    public List<AnnounceBean> getGacha() {
        return gacha;
    }

    public List<AnnounceBean> getEvent() {
        return event;
    }
}
