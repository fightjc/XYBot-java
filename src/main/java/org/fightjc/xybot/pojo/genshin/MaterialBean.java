package org.fightjc.xybot.pojo.genshin;

import java.util.List;

public class MaterialBean {
    String name;
    String description;
    int sortOrder;
    String rarity;
    String category;
    String materialType;
    String dropDomain;
    List<String> daysOfWeek;
    List<String> source;

    public MaterialBean(String name, String description, int sortOrder, String rarity, String category, String materialType,
                        String dropDomain, List<String> daysOfWeek, List<String> source) {
        this.name = name;
        this.description = description;
        this.sortOrder = sortOrder;
        this.rarity = rarity;
        this.category = category;
        this.materialType = materialType;
        this.dropDomain = dropDomain;
        this.daysOfWeek = daysOfWeek;
        this.source = source;
    }

    public String getMaterialType() {
        return materialType;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public List<String> getDaysOfWeek() {
        return daysOfWeek;
    }
}
