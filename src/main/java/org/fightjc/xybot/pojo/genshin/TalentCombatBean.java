package org.fightjc.xybot.pojo.genshin;

import java.util.List;
import java.util.Map;

public class TalentCombatBean {
    String name;
    String info;
    String description;
    Map<String, List<String>> attributes;

    public TalentCombatBean(String name, String info, String description, Map<String, List<String>> attributes) {
        this.name = name;
        this.info = info;
        this.description = description;
        this.attributes = attributes;
    }
}
