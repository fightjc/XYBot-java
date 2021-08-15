package org.fightjc.xybot.pojo.genshin;

import java.util.List;

public class MaterialResultDto {
    public String day;
    public List<MaterialInfo> talentMaterialList;
    public List<MaterialInfo> weaponMaterialList;

    public MaterialResultDto() {
    }

    public MaterialResultDto(String day, List<MaterialInfo> talentMaterialList, List<MaterialInfo> weaponMaterialList) {
        this.day = day;
        this.talentMaterialList = talentMaterialList;
        this.weaponMaterialList = weaponMaterialList;
    }

    public int getDayNum() {
        switch (day) {
            case "周一":
                return 1;
            case "周二":
                return 2;
            case "周三":
                return 3;
            case "周四":
                return 4;
            case "周五":
                return 5;
            case "周六":
                return 6;
            default:
                return 0;
        }
    }

    public static class MaterialInfo {
        public String region; // 国家
        public String location; // 秘境
        public String name; // 种类

        public NameMapBean star5;
        public NameMapBean star4;
        public NameMapBean star3;
        public NameMapBean star2;

        public List<NameMapBean> star5Result;
        public List<NameMapBean> star4Result;

        public MaterialInfo() {}
    }
}
