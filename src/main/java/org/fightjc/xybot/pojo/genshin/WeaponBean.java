package org.fightjc.xybot.pojo.genshin;

import java.util.List;
import java.util.Map;

public class WeaponBean {
    // 名称
    String name;
    // 武器介绍
    String description;
    // 装备类型
    String weaponType;
    // 星级
    String rarity;
    int baseAtk;
    String subStat;
    String subValue;

    //region 装备特效

    String effectName;
    String effect;
    List<String> r1;
    List<String> r2;
    List<String> r3;
    List<String> r4;
    List<String> r5;

    //endregion

    // 突破材料
    Map<String, List<CostBean>> costs;

    public WeaponBean(String name, String description, String weaponType, String rarity, int baseAtk, String subStat,
                      String subValue, String effectName, String effect, List<String> r1, List<String> r2,
                      List<String> r3, List<String> r4, List<String> r5, Map<String, List<CostBean>> costs) {
        this.name = name;
        this.description = description;
        this.weaponType = weaponType;
        this.rarity = rarity;
        this.baseAtk = baseAtk;
        this.subStat = subStat;
        this.subValue = subValue;
        this.effectName = effectName;
        this.effect = effect;
        this.r1 = r1;
        this.r2 = r2;
        this.r3 = r3;
        this.r4 = r4;
        this.r5 = r5;
        this.costs = costs;
    }
}
