package org.fightjc.xybot.pojo.genshin;

import java.util.List;

public class MaterialBean {
    String name;
    String description;
    String rarity;
    String category;
    String materialType;
    String dropDomain;
    List<String> daysOfWeek;
    List<String> source;

    public MaterialBean(String name, String description, String rarity, String category, String materialType,
                        String dropDomain, List<String> daysOfWeek, List<String> source) {
        this.name = name;
        this.description = description;
        this.rarity = rarity;
        this.category = category;
        this.materialType = materialType;
        this.dropDomain = dropDomain;
        this.daysOfWeek = daysOfWeek;
        this.source = source;
    }
}
