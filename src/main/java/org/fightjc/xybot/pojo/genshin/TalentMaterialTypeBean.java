package org.fightjc.xybot.pojo.genshin;

import java.util.List;

public class TalentMaterialTypeBean {
    String name;
    String star2Name;
    String star3Name;
    String star4Name;
    List<String> day;
    String location;
    String region;
    String domainOfMastery;

    public TalentMaterialTypeBean(String name, String star2Name, String star3Name, String star4Name, List<String> day,
                                  String location, String region, String domainOfMastery) {
        this.name = name;
        this.star2Name = star2Name;
        this.star3Name = star3Name;
        this.star4Name = star4Name;
        this.day = day;
        this.location = location;
        this.region = region;
        this.domainOfMastery = domainOfMastery;
    }
}
