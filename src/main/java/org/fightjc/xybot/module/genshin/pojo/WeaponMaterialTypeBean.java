package org.fightjc.xybot.module.genshin.pojo;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.List;

public class WeaponMaterialTypeBean {
    String name;
    @JSONField(name="2starname")
    String star2Name;
    @JSONField(name="3starname")
    String star3Name;
    @JSONField(name="4starname")
    String star4Name;
    @JSONField(name="5starname")
    String star5Name;
    List<String> day;
    String location;
    String region;
    String domainOfMastery;

    public WeaponMaterialTypeBean(String name, String star2Name, String star3Name, String star4Name, String star5Name,
                                  List<String> day, String location, String region, String domainOfMastery) {
        this.name = name;
        this.star2Name = star2Name;
        this.star3Name = star3Name;
        this.star4Name = star4Name;
        this.star5Name = star5Name;
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

    public String getStar5Name() {
        return star5Name;
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
            add(star5Name);
        }};
    }
}
