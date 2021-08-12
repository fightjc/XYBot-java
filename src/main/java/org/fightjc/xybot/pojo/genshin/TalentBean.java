package org.fightjc.xybot.pojo.genshin;

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
}
