package org.fightjc.xybot.pojo.genshin;

import java.util.List;
import java.util.Map;

public class CharacterBean {
    String name;
    String title;
    String description;
    String rarity;
    String elment;
    String weapontype;
    String substat;
    String gender;
    String body;
    String association;
    String region;
    String affiliation;
    String birthdaymmdd;
    String birthday;
    String constellation;
    Map<String, String> cv;
    Map<String, List<CostBean>> costs;

    public CharacterBean(String name, String title, String description, String rarity, String elment, String weapontype,
                         String substat, String gender, String body, String association, String region,
                         String affiliation, String birthdaymmdd, String birthday, String constellation,
                         Map<String, String> cv, Map<String, List<CostBean>> costs) {
        this.title = title;
        this.description = description;
        this.rarity = rarity;
        this.elment = elment;
        this.weapontype = weapontype;
        this.substat = substat;
        this.gender = gender;
        this.body = body;
        this.association = association;
        this.region = region;
        this.affiliation = affiliation;
        this.birthdaymmdd = birthdaymmdd;
        this.birthday = birthday;
        this.constellation = constellation;
        this.cv = cv;
        this.costs = costs;

    }
}
