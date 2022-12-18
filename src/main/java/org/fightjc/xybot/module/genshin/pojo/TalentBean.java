package org.fightjc.xybot.module.genshin.pojo;

import java.util.List;
import java.util.Map;

public class TalentBean {
    String name;
    TalentCombatBean combat1;
    TalentCombatBean combat2;
    TalentCombatBean combat3;
    TalentCombatBean combatSp;
    TalentPassiveBean passive1;
    TalentPassiveBean passive2;
    TalentPassiveBean passive3;
    Map<String, List<CostBean>> costs;

    public TalentBean(String name, TalentCombatBean combat1, TalentCombatBean combat2, TalentCombatBean combat3,
                      TalentCombatBean combatSp, TalentPassiveBean passive1, TalentPassiveBean passive2,
                      TalentPassiveBean passive3, Map<String, List<CostBean>> costs) {
        this.name = name;
        this.combat1 = combat1;
        this.combat2 = combat2;
        this.combat3 = combat3;
        this.combatSp = combatSp;
        this.passive1 = passive1;
        this.passive2 = passive2;
        this.passive3 = passive3;
        this.costs = costs;
    }

    public String getName() {
        return name;
    }

    public TalentCombatBean getCombat1() {
        return combat1;
    }

    public TalentCombatBean getCombat2() {
        return combat2;
    }

    public TalentCombatBean getCombat3() {
        return combat3;
    }

    public TalentCombatBean getCombatSp() {
        return combatSp;
    }

    public TalentPassiveBean getPassive1() {
        return passive1;
    }

    public TalentPassiveBean getPassive2() {
        return passive2;
    }

    public TalentPassiveBean getPassive3() {
        return passive3;
    }

    public Map<String, List<CostBean>> getCosts() {
        return costs;
    }

    public boolean needTalentMaterialType(TalentMaterialTypeBean talentMaterialType) {
        List<String> typeList = talentMaterialType.getAllTypeNames();
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

    public static class TalentCombatBean {
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

        public String getName() {
            return name;
        }

        public String getInfo() {
            return info;
        }
    }

    public static class TalentPassiveBean {
        String name;
        String info;

        public TalentPassiveBean(String name, String info) {
            this.name = name;
            this.info = info;
        }

        public String getName() {
            return name;
        }

        public String getInfo() {
            return info;
        }
    }
}
