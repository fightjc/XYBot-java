package org.fightjc.xybot.pojo.genshin;

import java.util.List;
import java.util.Map;

public class CharacterBean {
    String name;
    String title;
    String description;
    String rarity;
    String element;
    String weaponType;
    String subStat;
    String gender;
    String body;
    String association;
    String region;
    String affiliation;
    String birthDayMMDD;
    String birthday;
    String constellation;
    Map<String, String> cv;
    Map<String, List<CostBean>> costs;

    public CharacterBean(String name, String title, String description, String rarity, String element, String weaponType,
                         String subStat, String gender, String body, String association, String region,
                         String affiliation, String birthDayMMDD, String birthday, String constellation,
                         Map<String, String> cv, Map<String, List<CostBean>> costs) {
        this.name = name;
        this.title = title;
        this.description = description;
        this.rarity = rarity;
        this.element = element;
        this.weaponType = weaponType;
        this.subStat = subStat;
        this.gender = gender;
        this.body = body;
        this.association = association;
        this.region = region;
        this.affiliation = affiliation;
        this.birthDayMMDD = birthDayMMDD;
        this.birthday = birthday;
        this.constellation = constellation;
        this.cv = cv;
        this.costs = costs;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getRarity() {
        return rarity;
    }

    public String getElement() {
        return element;
    }

    public String getWeaponType() {
        return weaponType;
    }

    public String getSubStat() {
        return subStat;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getConstellation() {
        return constellation;
    }

    public Map<String, List<CostBean>> getCosts() {
        return costs;
    }
}
