package org.fightjc.xybot.pojo.genshin;

import java.util.List;

public class MaterialBean {
    String name;
    String description;
    String rarity;
    String category;
    String materialtype;
    String dropdomain;
    List<String> daysofweek;
    List<String> source;

    public MaterialBean(String name, String description, String rarity, String category, String materialtype,
                        String dropdomain, List<String> daysofweek, List<String> source) {
        this.name = name;
        this.description = description;
        this.rarity = rarity;
        this.category = category;
        this.materialtype = materialtype;
        this.dropdomain = dropdomain;
        this.daysofweek = daysofweek;
        this.source = source;
    }
}
