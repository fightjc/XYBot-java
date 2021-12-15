package org.fightjc.xybot.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.fightjc.xybot.po.HttpClientResult;
import org.fightjc.xybot.pojo.ResultOutput;
import org.fightjc.xybot.pojo.genshin.*;
import org.fightjc.xybot.service.GenshinService;
import org.fightjc.xybot.util.BotUtil;
import org.fightjc.xybot.util.HttpClientUtil;
import org.fightjc.xybot.util.genshin.GenshinMaterialDrawHelper;
import org.fightjc.xybot.util.genshin.GenshinSearchDrawHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class GenshinServiceImpl implements GenshinService {

    private static final Logger logger = LoggerFactory.getLogger(GenshinServiceImpl.class);

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
     * 通过名字查询角色或物品
     * @param name
     * @return
     */
    public ResultOutput<BufferedImage> getInfoByName(String name) {
        //region 角色
        JSONObject charObject = BotUtil.readJsonFile(BotUtil.getGenshinFolderPath() + "/index/characters.json");
        if (charObject == null) {
            logger.error("获取 /index/characters.json 对象失败");
            return new ResultOutput<>(false, "获取 /index/characters.json 对象失败");
        }

        // 角色名和路径字典
        Map<String, String> characterNameMap = charObject.getObject("names", new TypeReference<Map<String, String>>(){});
        if (characterNameMap == null) {
            logger.error("获取 /index/characters.json 中 names 对象失败");
            return new ResultOutput<>(false, "获取 /index/characters.json 中 names 对象失败");
        }

        // 判断是否是角色
        if (characterNameMap.containsKey(name)) {
            //return getCharacterInfo(characterNameMap.get(name));
            return new ResultOutput<>(false, "功能还在完善，敬请期待！", null);
        }
        //endregion

        //region 武器
        JSONObject weaponObject = BotUtil.readJsonFile(BotUtil.getGenshinFolderPath() + "/index/weapons.json");
        if (weaponObject == null) {
            logger.error("获取 /index/weapons.json 对象失败");
            return new ResultOutput<>(false, "获取 /index/weapons.json 对象失败");
        }

        // 武器名和路径字典
        Map<String, String> weaponNameMap = weaponObject.getObject("names", new TypeReference<Map<String, String>>(){});
        if (weaponNameMap == null) {
            logger.error("获取 /index/weapons.json 中 names 对象失败");
            return new ResultOutput<>(false, "获取 /index/weapons.json 中 names 对象失败");
        }

        // 判断是否是武器
        if (weaponNameMap.containsKey(name)) {
            return getWeaponInfo(weaponNameMap.get(name));
        }
        //endregion

        //region 材料
        JSONObject materialObject = BotUtil.readJsonFile(BotUtil.getGenshinFolderPath() + "/index/materials.json");
        if (materialObject == null) {
            logger.error("获取 /index/materials.json 对象失败");
            return new ResultOutput<>(false, "获取 /index/materials.json 对象失败");
        }

        // 材料名和路径字典
        Map<String, String> materialNameMap = materialObject.getObject("names", new TypeReference<Map<String, String>>(){});
        if (materialNameMap == null) {
            logger.error("获取 /index/materials.json 中 names 对象失败");
            return new ResultOutput<>(false, "获取 /index/materials.json 中 names 对象失败");
        }

        // 判断是否是材料
        if (materialNameMap.containsKey(name)) {
            //return getMaterialInfo(materialNameMap.get(name));
            return new ResultOutput<>(false, "功能还在完善，敬请期待！", null);
        }
        //endregion

        //region 模糊搜索
        List<String> itemList = new ArrayList() {{
            addAll(characterNameMap.keySet());
            addAll(weaponNameMap.keySet());
            addAll(materialNameMap.keySet());
        }};
        List<String> result = itemList.stream()
                .filter(s -> FuzzySearch.ratio(name, s) > 50)
                .collect(Collectors.toList());
        if (result.size() > 0) {
            return new ResultOutput<>(false, "找不到 " + name + " 的相关信息，也许你要找的是\n" + String.join("，", result));
        }
        //endregion

        return new ResultOutput<>(false, "找不到 " + name + " 的相关信息，以后即使知道也不告诉你～");
    }

    /**
     * 检查原神资源完整性
     * @return
     */
    public ResultOutput checkGenshinResource() {
        StringBuilder result = new StringBuilder();

        //region 检查角色
        JSONObject charObject = BotUtil.readJsonFile(BotUtil.getGenshinFolderPath() + "/index/characters.json");
        if (charObject == null) {
            logger.error("获取 /index/characters.json 对象失败");
            return new ResultOutput<>(false, "获取 /index/characters.json 对象失败");
        }

        // 角色名和路径字典
        Map<String, String> characterNameMap = charObject.getObject("names", new TypeReference<Map<String, String>>(){});
        if (characterNameMap == null) {
            logger.error("获取 /index/characters.json 中 names 对象失败");
            return new ResultOutput<>(false, "获取 /index/characters.json 中 names 对象失败");
        }

        for (String character : characterNameMap.keySet()) {
            String name = characterNameMap.get(character);
            // 数据
            String dataPath = BotUtil.getGenshinFolderPath() + "/data/characters/" + name + ".json";
            File dataFile = new File(dataPath);
            if (!dataFile.exists()) {
                result.append("\n 路径[").append(dataPath).append("]不存在");
            }
            // 图片
            String imagePath = BotUtil.getGenshinFolderPath() + "/images/characters/" + name + ".png";
            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                result.append("\n 路径[").append(imagePath).append("]不存在");
            }
        }
        //endregion

        //region 检查武器
        JSONObject weaponObject = BotUtil.readJsonFile(BotUtil.getGenshinFolderPath() + "/index/weapons.json");
        if (weaponObject == null) {
            logger.error("获取 /index/weapons.json 对象失败");
            return new ResultOutput<>(false, "获取 /index/weapons.json 对象失败");
        }

        // 武器名和路径字典
        Map<String, String> weaponNameMap = weaponObject.getObject("names", new TypeReference<Map<String, String>>(){});
        if (weaponNameMap == null) {
            logger.error("获取 /index/weapons.json 中 names 对象失败");
            return new ResultOutput<>(false, "获取 /index/weapons.json 中 names 对象失败");
        }

        for (String weapon : weaponNameMap.keySet()) {
            String name = weaponNameMap.get(weapon);
            // 数据
            String dataPath = BotUtil.getGenshinFolderPath() + "/data/weapons/" + name + ".json";
            File dataFile = new File(dataPath);
            if (!dataFile.exists()) {
                result.append("\n 路径[").append(dataPath).append("]不存在");
            }
            // 图片
            String imagePath = BotUtil.getGenshinFolderPath() + "/images/weapons/" + name + ".png";
            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                result.append("\n 路径[").append(imagePath).append("]不存在");
            }
        }
        //endregion

        //region 检查材料
        JSONObject materialObject = BotUtil.readJsonFile(BotUtil.getGenshinFolderPath() + "/index/materials.json");
        if (materialObject == null) {
            logger.error("获取 /index/materials.json 对象失败");
            return new ResultOutput<>(false, "获取 /index/materials.json 对象失败");
        }

        // 材料名和路径字典
        Map<String, String> materialNameMap = materialObject.getObject("names", new TypeReference<Map<String, String>>(){});
        if (materialNameMap == null) {
            logger.error("获取 /index/materials.json 中 names 对象失败");
            return new ResultOutput<>(false, "获取 /index/materials.json 中 names 对象失败");
        }

        for (String material : materialNameMap.keySet()) {
            String name = materialNameMap.get(material);
            // 数据
            String dataPath = BotUtil.getGenshinFolderPath() + "/data/materials/" + name + ".json";
            File dataFile = new File(dataPath);
            if (!dataFile.exists()) {
                result.append("\n 路径[").append(dataPath).append("]不存在");
            }
            // 图片
            String imagePath = BotUtil.getGenshinFolderPath() + "/images/materials/" + name + ".png";
            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                result.append("\n 路径[").append(imagePath).append("]不存在");
            }
        }
        //endregion

        //region 检查圣遗物
        // TODO: 圣遗物名称不统一
/*
        JSONObject artifactObject = BotUtil.readJsonFile(BotUtil.getGenshinFolderPath() + "/index/artifacts.json");
        if (artifactObject == null) {
            logger.error("获取 /index/artifacts.json 对象失败");
            return new ResultOutput<>(false, "获取 /index/artifacts.json 对象失败");
        }

        // 圣遗物名和路径字典
        Map<String, String> artifactNameMap = artifactObject.getObject("names", new TypeReference<Map<String, String>>(){});
        if (artifactNameMap == null) {
            logger.error("获取 /index/artifacts.json 中 names 对象失败");
            return new ResultOutput<>(false, "获取 /index/artifacts.json 中 names 对象失败");
        }

        for (String artifact : artifactNameMap.keySet()) {
            String filePath = artifactNameMap.get(artifact);
            // 数据
            String dataPath = BotUtil.getGenshinFolderPath() + "/data/artifacts/" + filePath;
            File dataFile = new File(dataPath);
            if (!dataFile.exists()) {
                result.append("\n 路径[").append(dataPath).append("]不存在");
            }
            // 图片
            String name = filePath.substring(0, filePath.indexOf("."));
            List<String> imagePaths = new ArrayList<String>() {{
                add("_circlet.png");
                add("_flower.png");
                add("_goblet.png");
                add("_plume.png");
                add("_sands.png");
            }};
            for (String suffix : imagePaths) {
                String imagePath = BotUtil.getGenshinFolderPath() + "/images/artifacts/" + name + suffix;
                File imageFile = new File(imagePath);
                if (!imageFile.exists()) {
                    result.append("\n 路径[").append(imagePath).append("]不存在");
                }
            }
        }
 */
        //endregion

        if (result.length() > 0) {
            return new ResultOutput<>(false, result.toString());
        } else {
            return new ResultOutput<>(true, "恭喜，原神资源完整！");
        }
    }

    /**
     * 获取原神日历
     * @return
     */
    public ResultOutput<BufferedImage> getCalendar() {
        return new ResultOutput<>(true, "");
    }

    /**
     * 从资源文件中提取每日材料整合数据结果
     * @return
     */
    private ResultOutput<List<MaterialResultDto>> getMaterialResult() {
        Map<String, String> characterNameMap; // 角色名字典
        Map<String, String> weaponNameMap; // 武器名字典
        Map<String, String> materialNameMap; // 材料名字典

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
                        BotUtil.getGenshinFolderPath() + "/data/talentmaterialtypes/" + tmtArray.getString(j) + ".json",
                        TalentMaterialTypeBean.class);
                if (tmtBean != null) {
                    tmtList.add(tmtBean);
                } else {
                    logger.error("获取 /data/talentmaterialtypes/" + tmtArray.getString(j) + ".json 对象失败");
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
                        BotUtil.getGenshinFolderPath() + "/data/weaponmaterialtypes/" + wmtArray.getString(j) + ".json",
                        WeaponMaterialTypeBean.class);
                if (wmtBean != null){
                    wmtList.add(wmtBean);
                } else {
                    logger.error("获取 /data/weaponmaterialtypes/" + wmtArray.getString(j) + ".json 对象失败");
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
                        BotUtil.getGenshinFolderPath() + "/data/talents/" + charArray.getString(j) + ".json",
                        TalentBean.class);
                if (talentBean != null) {
                    talentList.add(talentBean);
                } else {
                    logger.error("获取 /data/talents/" + charArray.getString(j) + ".json 对象失败");
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
                        BotUtil.getGenshinFolderPath() + "/data/weapons/" + weaponArray.getString(j) + ".json",
                        WeaponBean.class);
                if (weaponBean != null) {
                    weaponList.add(weaponBean);
                } else {
                    logger.error("获取 /data/weapons/" + weaponArray.getString(j) + ".json 对象失败");
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

    /**
     * 获取角色信息
     * @param name
     * @return
     */
    private ResultOutput<BufferedImage> getCharacterInfo(String name) {
        // 角色主属性信息
        CharacterBean characterBean = BotUtil.readJsonFile(
                BotUtil.getGenshinFolderPath() + "/data/characters/" + name + ".json",
                CharacterBean.class);
        if (characterBean == null) {
            logger.error("获取 /data/characters/" + name + ".json 对象失败");
            return new ResultOutput<>(false, "获取 /data/characters/" + name + ".json 对象失败");
        }

        // 角色天赋
        TalentBean talentBean = BotUtil.readJsonFile(
                BotUtil.getGenshinFolderPath() + "/data/talents/" + name + ".json",
                TalentBean.class);
        if (talentBean == null) {
            logger.error("获取 /data/talents/" + name + ".json 对象失败");
            return new ResultOutput<>(false, "获取 /data/talents/" + name + ".json 对象失败");
        }

        // 角色命座
        ConstellationBean constellationBean = BotUtil.readJsonFile(
                BotUtil.getGenshinFolderPath() + "/data/constellations/" + name + ".json",
                ConstellationBean.class);
        if (constellationBean == null) {
            logger.error("获取 /data/constellations/" + name + ".json 对象失败");
            return new ResultOutput<>(false, "获取 /data/constellations/" + name + ".json 对象失败");
        }

        // 角色图标
        String imagePath = BotUtil.getGenshinFolderPath() + "/images/characters/" + name + ".png";
        BufferedImage image = BotUtil.readImageFile(imagePath);
        if (image == null) {
            logger.error("获取 /images/characters/" + name + ".png 对象失败");
            return new ResultOutput<>(false, "获取 /images/characters/" + name + ".png 对象失败");
        }

        //TODO: 太长了卡片显示不全
//        String info =
//                "{" +
//                    "\"app\":\"com.tencent.miniapp\"," +
//                    "\"view\":\"notification\"," +
//                    "\"prompt\":\"角色\"," +
//                    "\"desc\":\"\"," +
//                    "\"ver\":\"0.0.0.1\"," +
//                    "\"meta\":{" +
//                        "\"notification\":{" +
//                            "\"appInfo\":{" +
//                                "\"appName\":\""+ characterBean.getName() + "\"," +
//                                "\"appType\":4," +
//                                "\"appid\":1109659848," +
//                                "\"iconUrl\":\"" + characterMap.getString("icon") + "\"" +
//                            "}," +
//                            "\"data\":[" +
//                                "{\"title\":\"星级\",\"value\":\"" + characterBean.getRarity() +"\"}," +
//                                "{\"title\":\"神之眼\",\"value\":\"" + characterBean.getElement() + "\"}," +
//                                "{\"title\":\"武器类型\",\"value\":\"" + characterBean.getWeaponType() + "\"}," +
//                                "{\"title\":\"升级属性\",\"value\":\"" + characterBean.getSubStat() + "\"}," +
//                                "{\"title\":\"生日\",\"value\":\"" + characterBean.getBirthday() + "\"}," +
//                                "{\"title\":\"天赋\",\"value\":\"\"}," +
//                                "{\"title\":\"" + talentBean.getCombat1().getName() + "\",\"value\":\"" + talentBean.getCombat1().getInfo() + "\"}," +
//                                "{\"title\":\"" + talentBean.getCombat2().getName() + "\",\"value\":\"" + talentBean.getCombat2().getInfo() + "\"}," +
//                                "{\"title\":\"" + talentBean.getCombat3().getName() + "\",\"value\":\"" + talentBean.getCombat3().getInfo() + "\"}," +
//                                (talentBean.getCombatSp() == null ?
//                                        "" : "{\"title\":\"" + talentBean.getCombatSp().getName() + "\",\"value\":\"" + talentBean.getCombatSp().getInfo() + "\"},") +
//                                "{\"title\":\"" + talentBean.getPassive1().getName() + "\",\"value\":\"" + talentBean.getPassive1().getInfo() + "\"}," +
//                                "{\"title\":\"" + talentBean.getPassive2().getName() + "\",\"value\":\"" + talentBean.getPassive2().getInfo() + "\"}," +
//                                (talentBean.getPassive3() == null ?
//                                        "" : "{\"title\":\"" + talentBean.getPassive3().getName() + "\",\"value\":\"" + talentBean.getPassive3().getInfo() + "\"},") +
//                                "{\"title\":\"命之座\",\"value\":\"-\"}," +
//                                "{\"title\":\"1." + constellationBean.getC1().getName() + "\",\"value\":\"" + constellationBean.getC1().getEffect() + "\"}," +
//                                "{\"title\":\"2." + constellationBean.getC2().getName() + "\",\"value\":\"" + constellationBean.getC2().getEffect() + "\"}," +
//                                "{\"title\":\"3." + constellationBean.getC3().getName() + "\",\"value\":\"" + constellationBean.getC3().getEffect() + "\"}," +
//                                "{\"title\":\"4." + constellationBean.getC4().getName() + "\",\"value\":\"" + constellationBean.getC4().getEffect() + "\"}," +
//                                "{\"title\":\"5." + constellationBean.getC5().getName() + "\",\"value\":\"" + constellationBean.getC5().getEffect() + "\"}," +
//                                "{\"title\":\"6." + constellationBean.getC6().getName() + "\",\"value\":\"" + constellationBean.getC6().getEffect() + "\"}" +
//                            "]" +
//                        "}" +
//                    "}" +
//                "}";
//        info = info.replace("\n", "\\n");

        return new ResultOutput<>(true, "", GenshinSearchDrawHelper.drawCharacterInfo());
    }

    /**
     *  获取武器信息
     * @param name
     * @return
     */
    private ResultOutput<BufferedImage> getWeaponInfo(String name) {
        // 武器信息
        WeaponBean weaponBean = BotUtil.readJsonFile(
                BotUtil.getGenshinFolderPath() + "/data/weapons/" + name + ".json",
                WeaponBean.class);
        if (weaponBean == null) {
            logger.error("获取 /data/weapons/" + name + ".json 对象失败");
            return new ResultOutput<>(false, "获取 /data/weapons/" + name + ".json 对象失败");
        }

        // 武器图标
        String imagePath = BotUtil.getGenshinFolderPath() + "/images/weapons/" + name + ".png";
        BufferedImage image = BotUtil.readImageFile(imagePath);
        if (image == null) {
            logger.error("获取 /images/weapons/" + name + ".png 对象失败");
            return new ResultOutput<>(false, "获取 /images/weapons/" + name + ".png 对象失败");
        }

        // 获取材料索引
        JSONObject materialObject = BotUtil.readJsonFile(BotUtil.getGenshinFolderPath() + "/index/materials.json");
        if (materialObject == null) {
            logger.error("获取 /index/materials.json 对象失败");
            return new ResultOutput<>(false, "获取 /index/materials.json 对象失败");
        }

        // 材料名和路径字典
        Map<String, String> materialNameMap = materialObject.getObject("names", new TypeReference<Map<String, String>>(){});
        if (materialNameMap == null) {
            logger.error("获取 /index/materials.json 中 names 对象失败");
            return new ResultOutput<>(false, "获取 /index/materials.json 中 names 对象失败");
        }

        // 武器突破材料表
        Map<String, MaterialBean> materialMap = new HashMap<>(); // 存储当前材料详细信息
        Map<String, Map<String, Integer>> totalMaterialMap = new HashMap<>(); // 以材料类别和材料名分类
        Map<String, List<CostBean>> costMap = weaponBean.getCosts();
        for (String key : costMap.keySet()) {
            List<CostBean> costList = costMap.get(key);
            for (CostBean cost : costList) {
                String materialName = cost.getName();

                if (!materialMap.containsKey(materialName)) {
                    String materialPath = materialNameMap.get(materialName);

                    MaterialBean bean = BotUtil.readJsonFile(BotUtil.getGenshinFolderPath() + "/data/materials/" + materialPath + ".json", MaterialBean.class);
                    materialMap.put(materialName, bean);
                }

                MaterialBean material = materialMap.get(materialName);
                String type = material.getMaterialType();
                int count = cost.getCount();

                // 统计累加数量
                Map<String, Integer> materialSumMap = new HashMap<>();
                if (totalMaterialMap.containsKey(type)) {
                    materialSumMap = totalMaterialMap.get(type);
                }
                if (materialSumMap.containsKey(materialName)) {
                    count += materialSumMap.get(materialName);
                }
                materialSumMap.put(materialName, count);
                totalMaterialMap.put(type, materialSumMap);
            }
        }
        // 组装材料dto
        Map<String, Integer> adsorbateMap =  totalMaterialMap.get("通用货币");
        CostDto mora = new CostDto("摩拉", adsorbateMap.get("摩拉"),
                BotUtil.readImageFile(BotUtil.getGenshinFolderPath() + "/images/materials/mora.png"),
                0, "");
        Map<String, Integer> avatarMap =  totalMaterialMap.get("角色培养素材");
        List<CostDto> avatarList = new ArrayList<>();
        for (String key : avatarMap.keySet()) {
            String materialFilePath = materialNameMap.get(key);
            CostDto cost = new CostDto(key, avatarMap.get(key),
                    BotUtil.readImageFile(BotUtil.getGenshinFolderPath() + "/images/materials/" + materialFilePath + ".png"),
                    materialMap.get(key).getSortOrder(), "");
            avatarList.add(cost);
        }
        avatarList.sort(Comparator.comparing(CostDto::getSort)); // 根据sortOrder排序
        Map<String, Integer> weaponMap =  totalMaterialMap.get("武器突破素材");
        List<CostDto> weaponList = new ArrayList<>();
        for (String key : weaponMap.keySet()) {
            String materialFilePath = materialNameMap.get(key);
            MaterialBean materialBean = materialMap.get(key);
            String info = "（" + String.join("/", materialBean.getDaysOfWeek()) + "）"; // 刷取日期
            CostDto cost = new CostDto(key, weaponMap.get(key),
                    BotUtil.readImageFile(BotUtil.getGenshinFolderPath() + "/images/materials/" + materialFilePath + ".png"),
                    materialBean.getSortOrder(), info);
            weaponList.add(cost);
        }
        weaponList.sort(Comparator.comparing(CostDto::getSort)); // 根据sortOrder排序

        BufferedImage target = GenshinSearchDrawHelper.drawWeaponInfo(new WeaponDrawDto(image, weaponBean,
                mora, avatarList, weaponList));
        return new ResultOutput<>(true, "", target);
    }

    /**
     * 获取材料信息
     * @param name
     * @return
     */
    private ResultOutput<BufferedImage> getMaterialInfo(String name) {
        return new ResultOutput<>(true, "", GenshinSearchDrawHelper.drawMaterialInfo());
    }

    /**
     * 获取原神游戏内公告
     * @return
     */
    private ResultOutput<String> getGenshinAnnouncement() {
        String url = "https://hk4e-api.mihoyo.com/common/hk4e_cn/announcement/api/getAnnList";

        Map<String, String> params = new HashMap<String, String>() {{
            put("game", "hk4e");
            put("game_biz", "hk4e_cn");
            put("lang", "zh-cn");
            put("bundle_id", "hk4e_cn");
            put("platform", "pc");
            put("region", "cn_gf01");
            put("level", "55");
            put("uid", "100000000");
        }};

        HttpClientResult httpClientResult;
        try {
            httpClientResult = HttpClientUtil.doGet(url, null, params);
            JSONObject content = JSONObject.parseObject(httpClientResult.content);

            //TODO:

            return new ResultOutput<>(true, "查询成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultOutput<>(false, "请求网络失败");
        }
    }
}
