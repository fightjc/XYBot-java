package org.fightjc.xybot.module.genshin.pojo;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.List;

public class TalentMaterialTypeBean {
    String name;
    @JSONField(name="2starname")
    String star2Name;
    @JSONField(name="3starname")
    String star3Name;
    @JSONField(name="4starname")
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

    public String getName() {
        return name;
    }

    public String getStar2Name() {
        return star2Name;
    }

    public String getStar3Name() {
        return star3Name;
    }

    public String getStar4Name() {
        return star4Name;
    }

    public String getLocation() {
        return location;
    }

    public String getRegion() {
        return region;
    }

    public List<String> getAllTypeNames() {
        return new ArrayList<String>() {{
            add(star2Name);
            add(star3Name);
            add(star4Name);
        }};
    }
}
