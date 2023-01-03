package org.fightjc.xybot.model;

import lombok.Getter;

@Getter
public class Gacha {

    String title;

    String content;

    public Gacha(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
