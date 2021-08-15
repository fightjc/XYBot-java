package org.fightjc.xybot.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.fightjc.xybot.pojo.ResultOutput;
import org.fightjc.xybot.pojo.genshin.*;
import org.fightjc.xybot.service.GenshinService;
import org.fightjc.xybot.util.BotUtil;
import org.fightjc.xybot.util.genshin.GenshinMaterialDrawHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GenshinServiceImpl implements GenshinService {

    private static final Logger logger = LoggerFactory.getLogger(GenshinServiceImpl.class);

    @Autowired
    @Qualifier("roleImageMap")
    Map<String, BufferedImage> roleImageMap;

    @Autowired
    @Qualifier("materialImageMap")
    Map<String, BufferedImage> materialImageMap;

    @Autowired
    @Qualifier("weaponImageMap")
    Map<String, BufferedImage> weaponImageMap;

    /**
     * 星期枚举
     */
    public enum DAILY_MATERIAL_WEEK {
        SUN(0), MON(1), TUE(2), WED(3), THU(4), FRI(5), SAT(6);

        int id;

        DAILY_MATERIAL_WEEK(int i) { id = i; }

        public boolean Compare(int i) { return id == i; }

        public static DAILY_MATERIAL_WEEK getWeek(int w) {
            DAILY_MATERIAL_WEEK[] values = DAILY_MATERIAL_WEEK.values();
            for (DAILY_MATERIAL_WEEK value : values) {
                if (value.Compare(w)) return value;
            }
            return null;
        }
    }

    /**
     * 获取每日素材汇总图片
     * @param day
     * @return
     */
    public ResultOutput<BufferedImage> getDailyMaterial(DAILY_MATERIAL_WEEK day) {
        if (day == DAILY_MATERIAL_WEEK.SUN) {
            return new ResultOutput<>(true, "笨蛋，周日啥资源都有哦～");
        }

        String pngPath = BotUtil.getGenshinFolderPath() + "/dailymaterial_" + day.id + ".png";
        BufferedImage rawImage = BotUtil.readImageFile(pngPath);

        if (rawImage == null) {
            return new ResultOutput<>(false, "读取图片资源失败");
        }

        return new ResultOutput<>(true, "读取图片资源成功", rawImage);
    }

    /**
     * 更新每日素材汇总图片
     * @return
     */
    public ResultOutput updateDailyMaterial() {
        // 获取每日材料整合数据结果
        ResultOutput<List<MaterialResultDto>> materialResult = getMaterialResult();
        if (!materialResult.getSuccess()) {
            return materialResult;
        }

        // 更新每日素材图片
        ResultOutput drawResult = GenshinMaterialDrawHelper.drawDailyMaterial(materialResult.getObject());
        if (!drawResult.getSuccess()) {
            return drawResult;
        }

        return new ResultOutput<>(true, "更新每日素材图片成功");
    }

    /**
     * 从资源文件中提取每日材料整合数据结果
     * @return
     */
    private ResultOutput<List<MaterialResultDto>> getMaterialResult() {
        Map<String, String> characterNameMap; // 角色名和路径字典
        Map<String, String> weaponNameMap; // 武器名和路径字典
        Map<String, String> materialNameMap; // 材料名和路径字典

        Map<String, List<TalentMaterialTypeBean>> tmtMap = new HashMap<>(); // 天赋材料字典，按星期分类
        Map<String, List<WeaponMaterialTypeBean>> wmtMap = new HashMap<>(); // 武器材料字典，按星期分类
        Map<String, List<TalentBean>> talentMap = new HashMap<>(); // 天赋字典，按星级分类
        Map<String, List<WeaponBean>> weaponMap = new HashMap<>(); // 武器字典，按星级分类

        //region 获取游戏基础数据数组

        JSONObject categoriesObject = BotUtil.readJsonFile(BotUtil.getGenshinFolderPath() + "/data/categories.json");
        if (categoriesObject == null) {
            logger.error("获取 /data/categories.json 对象失败");
            return new ResultOutput<>(false, "获取 /data/categories.json 对象失败");
        }

        // 星期
        JSONArray dayArray = categoriesObject.getJSONArray("day");
        if (dayArray == null) {
            logger.error("获取 /data/categories.json 中 day 对象失败");
            return new ResultOutput<>(false, "获取 /data/categories.json 中 day 对象失败");
        }
        List<String> dayList = dayArray.toJavaList(String.class);

        // 国家
        JSONArray regionArray = categoriesObject.getJSONArray("region");
        if (regionArray == null) {
            logger.error("获取 /data/categories.json 中 region 对象失败");
            return new ResultOutput<>(false, "获取 /data/categories.json 中 region 对象失败");
        }
        List<String> regionList = regionArray.toJavaList(String.class);

        // 星级
        JSONArray rarityArray = categoriesObject.getJSONArray("rarity");
        if (rarityArray == null) {
            logger.error("获取 /data/categories.json 中 rarity 对象失败");
            return new ResultOutput<>(false, "获取 /data/categories.json 中 rarity 对象失败");
        }
        List<String> rarityList = rarityArray.toJavaList(String.class);

        //endregion

        //region 获取材料名和路径字典

        JSONObject materialObject = BotUtil.readJsonFile(BotUtil.getGenshinFolderPath() + "/index/materials.json");
        if (materialObject == null) {
            logger.error("获取 /index/materials.json 对象失败");
            return new ResultOutput<>(false, "获取 /index/materials.json 对象失败");
        }

        // 材料名和路径字典
        materialNameMap = materialObject.getObject("names", new TypeReference<Map<String, String>>(){});
        if (materialNameMap == null) {
            logger.error("获取 /index/materials.json 中 names 对象失败");
            return new ResultOutput<>(false, "获取 /index/materials.json 中 names 对象失败");
        }

        //endregion

        //region 获取天赋材料字典，按星期分类

        JSONObject tmtObject = BotUtil.readJsonFile(BotUtil.getGenshinFolderPath() + "/index/talentmaterialtypes.json");
        if (tmtObject == null) {
            logger.error("获取 /index/talentmaterialtypes.json 对象失败");
            return new ResultOutput<>(false, "获取 /index/talentmaterialtypes.json 对象失败");
        }

        JSONObject tmtCategories = tmtObject.getJSONObject("categories");
        if (tmtCategories == null) {
            logger.error("获取 /index/talentmaterialtypes.json 中 categories 对象失败");
            return new ResultOutput<>(false, "获取 /index/talentmaterialtypes.json 中 categories 对象失败");
        }

        // 根据星期生成字典
        for (String day : dayList) {
            JSONArray tmtArray = tmtCategories.getJSONArray(day);
            if (tmtArray == null) continue;
            List<TalentMaterialTypeBean> tmtList = new ArrayList<>();
            for (int j = 0; j < tmtArray.size(); j++) {
                TalentMaterialTypeBean tmtBean = BotUtil.readJsonFile(
                        BotUtil.getGenshinFolderPath() + "/data/talentmaterialtypes/" + tmtArray.getString(j),
                        TalentMaterialTypeBean.class);
                if (tmtBean != null) {
                    tmtList.add(tmtBean);
                } else {
                    logger.error("获取 /data/talentmaterialtypes/" + tmtArray.getString(j) + " 对象失败");
                }
            }
            tmtMap.put(day, tmtList);
        }

        //endregion

        //region 获取武器材料字典，按星期分类

        JSONObject wmtObject = BotUtil.readJsonFile(BotUtil.getGenshinFolderPath() + "/index/weaponmaterialtypes.json");
        if (wmtObject == null) {
            logger.error("获取 /index/weaponmaterialtypes.json 对象失败");
            return new ResultOutput<>(false, "获取 /index/weaponmaterialtypes.json 对象失败");
        }

        JSONObject wmtCategories = wmtObject.getJSONObject("categories");
        if (wmtCategories == null) {
            logger.error("获取 /index/weaponmaterialtypes.json 中 categories 对象失败");
            return new ResultOutput<>(false, "获取 /index/weaponmaterialtypes.json 中 categories 对象失败");
        }

        // 根据星期生成字典
        for (String day : dayList) {
            JSONArray wmtArray = wmtCategories.getJSONArray(day);
            List<WeaponMaterialTypeBean> wmtList = new ArrayList<>();
            for (int j = 0; j < wmtArray.size(); j++) {
                WeaponMaterialTypeBean wmtBean = BotUtil.readJsonFile(
                        BotUtil.getGenshinFolderPath() + "/data/weaponmaterialtypes/" + wmtArray.getString(j),
                        WeaponMaterialTypeBean.class);
                if (wmtBean != null){
                    wmtList.add(wmtBean);
                } else {
                    logger.error("获取 /data/weaponmaterialtypes/" + wmtArray.getString(j) + " 对象失败");
                }
            }
            wmtMap.put(day, wmtList);
        }

        //endregion

        //region 获取天赋字典，按星级分类

        JSONObject charObject = BotUtil.readJsonFile(BotUtil.getGenshinFolderPath() + "/index/characters.json");
        if (charObject == null) {
            logger.error("获取 /index/characters.json 对象失败");
            return new ResultOutput<>(false, "获取 /index/characters.json 对象失败");
        }

        // 角色名和路径字典
        characterNameMap = charObject.getObject("names", new TypeReference<Map<String, String>>(){});
        if (characterNameMap == null) {
            logger.error("获取 /index/characters.json 中 names 对象失败");
            return new ResultOutput<>(false, "获取 /index/characters.json 中 names 对象失败");
        }

        JSONObject charCategories = charObject.getJSONObject("categories");
        if (charCategories == null) {
            logger.error("获取 /index/characters.json 中 categories 对象失败");
            return new ResultOutput<>(false, "获取 /index/characters.json 中 categories 对象失败");
        }

        // 根据星级生成字典
        for (String rarity : rarityList) {
            JSONArray charArray = charCategories.getJSONArray(rarity);
            if (charArray == null) continue;
            List<TalentBean> talentList = new ArrayList<>();
            for (int j = 0; j < charArray.size(); j++) {
                TalentBean talentBean = BotUtil.readJsonFile(
                        BotUtil.getGenshinFolderPath() + "/data/talents/" + charArray.getString(j),
                        TalentBean.class);
                if (talentBean != null) {
                    talentList.add(talentBean);
                } else {
                    logger.error("获取 /data/talents/" + charArray.getString(j) + " 对象失败");
                }
            }
            talentMap.put(rarity, talentList);
        }

        //endregion，按星级分类

        //region 获取武器字典，按星级分类

        JSONObject weaponObject = BotUtil.readJsonFile(BotUtil.getGenshinFolderPath() + "/index/weapons.json");
        if (weaponObject == null) {
            logger.error("获取 /index/weapons.json 对象失败");
            return new ResultOutput<>(false, "获取 /index/weapons.json 对象失败");
        }

        // 武器名和路径字典
        weaponNameMap = weaponObject.getObject("names", new TypeReference<Map<String, String>>(){});
        if (weaponNameMap == null) {
            logger.error("获取 /index/weapons.json 中 names 对象失败");
            return new ResultOutput<>(false, "获取 /index/weapons.json 中 names 对象失败");
        }

        JSONObject weaponCategories = weaponObject.getJSONObject("categories");
        if (weaponCategories == null) {
            logger.error("获取 /index/weapons.json 中 categories 对象失败");
            return new ResultOutput<>(false, "获取 /index/weapons.json 中 categories 对象失败");
        }

        // 根据星级生成字典
        for (String rarity : rarityList) {
            JSONArray weaponArray = weaponCategories.getJSONArray(rarity);
            if (weaponArray == null) continue;
            List<WeaponBean> weaponList = new ArrayList<>();
            for (int j = 0; j < weaponArray.size(); j++) {
                WeaponBean weaponBean = BotUtil.readJsonFile(
                        BotUtil.getGenshinFolderPath() + "/data/weapons/" + weaponArray.getString(j),
                        WeaponBean.class);
                if (weaponBean != null) {
                    weaponList.add(weaponBean);
                } else {
                    logger.error("获取 /data/weapons/" + weaponArray.getString(j) + " 对象失败");
                }
            }
            weaponMap.put(rarity, weaponList);
        }

        //endregion

        //region 生成每日材料整合数据

        List<MaterialResultDto> materialResultDtoList = new ArrayList<>();

        for (String day : dayList) {
            if (day.equals("周日")) continue;
            List<TalentMaterialTypeBean> tmtList = tmtMap.get(day);
            List<WeaponMaterialTypeBean> wmtList = wmtMap.get(day);

            MaterialResultDto materialResultDto = new MaterialResultDto();
            materialResultDto.day = day;
            materialResultDto.talentMaterialList = new ArrayList<>();
            materialResultDto.weaponMaterialList = new ArrayList<>();

            for (String region : regionList) {
                // 天赋
                if (tmtList != null) {
                    for (TalentMaterialTypeBean tmt : tmtList) {
                        if (!tmt.getRegion().equals(region)) continue;

                        MaterialResultDto.MaterialInfo talentMaterial = new MaterialResultDto.MaterialInfo();
                        talentMaterial.region = tmt.getRegion();
                        talentMaterial.location = tmt.getLocation();
                        talentMaterial.name = tmt.getName();
                        talentMaterial.star2 = new NameMapBean(tmt.getStar2Name(), materialNameMap.get(tmt.getStar2Name()));
                        talentMaterial.star3 = new NameMapBean(tmt.getStar3Name(), materialNameMap.get(tmt.getStar3Name()));
                        talentMaterial.star4 = new NameMapBean(tmt.getStar4Name(), materialNameMap.get(tmt.getStar4Name()));

                        for (String rarity : rarityList) {
                            if (!rarity.equals("4") && !rarity.equals("5")) continue; // 只显示4星和5星角色

                            List<TalentBean> talentList = talentMap.get(rarity);
                            if (talentList == null) continue;

                            List<NameMapBean> starResult = new ArrayList<>();
                            for (TalentBean talent : talentList) {
                                if (talent.needTalentMaterialType(tmt)) {
                                    starResult.add(new NameMapBean(talent.getName(), characterNameMap.get(talent.getName())));
                                }
                            }

                            if (rarity.equals("4")) {
                                talentMaterial.star4Result = starResult;
                            } else {
                                talentMaterial.star5Result = starResult;
                            }
                        }

                        materialResultDto.talentMaterialList.add(talentMaterial);
                    }
                }

                // 武器
                if (wmtList != null) {
                    for (WeaponMaterialTypeBean wmt : wmtList) {
                        if (!wmt.getRegion().equals(region)) continue;

                        MaterialResultDto.MaterialInfo weaponMaterial = new MaterialResultDto.MaterialInfo();
                        weaponMaterial.region = wmt.getRegion();
                        weaponMaterial.location = wmt.getLocation();
                        weaponMaterial.name = wmt.getName();
                        weaponMaterial.star2 = new NameMapBean(wmt.getStar2Name(), materialNameMap.get(wmt.getStar2Name()));
                        weaponMaterial.star3 = new NameMapBean(wmt.getStar3Name(), materialNameMap.get(wmt.getStar3Name()));
                        weaponMaterial.star4 = new NameMapBean(wmt.getStar4Name(), materialNameMap.get(wmt.getStar4Name()));
                        weaponMaterial.star5 = new NameMapBean(wmt.getStar5Name(), materialNameMap.get(wmt.getStar5Name()));

                        for (String rarity : rarityList) {
                            if (!rarity.equals("4") && !rarity.equals("5")) continue; // 只显示4星和5星武器

                            List<WeaponBean> weaponList = weaponMap.get(rarity);
                            if (weaponList == null) continue;

                            List<NameMapBean> starResult = new ArrayList<>();
                            for (WeaponBean weapon : weaponList) {
                                if (weapon.needWeaponMaterialType(wmt)) {
                                    starResult.add(new NameMapBean(weapon.getName(), weaponNameMap.get(weapon.getName())));
                                }
                            }

                            if (rarity.equals("4")) {
                                weaponMaterial.star4Result = starResult;
                            } else {
                                weaponMaterial.star5Result = starResult;
                            }
                        }

                        materialResultDto.weaponMaterialList.add(weaponMaterial);
                    }
                }
            }

            materialResultDtoList.add(materialResultDto);
        }

        //endregion

        return new ResultOutput<>(true, "生成每日素材整合数据成功", materialResultDtoList);
    }
}
