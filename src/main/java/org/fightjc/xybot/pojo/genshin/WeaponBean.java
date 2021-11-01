package org.fightjc.xybot.pojo.genshin;

import java.util.ArrayList;
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
    // 基础攻击
    int baseAtk;
    // 副词缀
    String subStat;
    // 副词缀初始值
    String subValue;
    // 效果名称
    String effectName;
    // 效果描述
    String effect;
    // 精炼1
    List<String> r1;
    // 精炼2
    List<String> r2;
    // 精炼3
    List<String> r3;
    // 精炼4
    List<String> r4;
    // 精炼5
    List<String> r5;
    // 突破材料，一星和二星装备只有4条
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

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getWeaponType() {
        return weaponType;
    }

    public String getRarity() {
        return rarity;
    }

    public String getSubStat() {
        return subStat;
    }

    public String getEffectName() {
        return effectName;
    }

    public String getLongEffect() {
        String result = effect;
        int count = r1.size();
        for (int i = 0; i < count; i++) {
            String sb = r1.get(i) +
                    "/" + r2.get(i) +
                    "/" + r3.get(i) +
                    "/" + r4.get(i) +
                    "/" + r5.get(i);
            result = result.replace("{" + i + "}", "[" + sb + "]");
        }

        return result;
    }

    public boolean needWeaponMaterialType(WeaponMaterialTypeBean weaponMaterialType) {
        List<String> typeList = weaponMaterialType.getAllTypeNames();
        for (String key : costs.keySet()) {
            List<CostBean> costList = costs.get(key);
            for (CostBean cost : costList) {
                if (typeList.contains(cost.getName())) {
                    return true;
                }
            }
        }
        return false;
    }
}
