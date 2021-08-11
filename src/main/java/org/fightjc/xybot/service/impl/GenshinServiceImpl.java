package org.fightjc.xybot.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.fightjc.xybot.pojo.Gacha;
import org.fightjc.xybot.pojo.ResultOutput;
import org.fightjc.xybot.pojo.genshin.CostBean;
import org.fightjc.xybot.pojo.genshin.TalentBean;
import org.fightjc.xybot.pojo.genshin.TalentMaterialTypeBean;
import org.fightjc.xybot.pojo.genshin.WeaponMaterialTypeBean;
import org.fightjc.xybot.service.GenshinService;
import org.fightjc.xybot.util.BotUtil;
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

        String pngPath = BotUtil.getResourceFolderPath() + "/genshin/dailymaterial_" + day.id + ".png";
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
    public ResultOutput<String> updateDailyMaterial() {
        return new ResultOutput<>(true, "更新每日素材图片成功");
    }

    private void getMaterialTypes() {
        List<String> dayList; // 星期数组
        List<String> regionList; // 材料来源国家数组
        List<String> rarityList; // 星级数组
        Map<String, List<TalentBean>> talentBeanList = new HashMap<>(); // 角色天赋字典，按星级分类
        Map<String, List<TalentMaterialTypeBean>> tmtMap = new HashMap<>(); // 天赋材料字典，按星期分类
        Map<String, List<WeaponMaterialTypeBean>> wmtMap = new HashMap<>(); // 武器材料字典，按星期分类
        String genshinFolderPath =  BotUtil.getGenshinFolderPath();

        // 获取游戏基本数据数组
        JSONObject categoriesObject = BotUtil.readJsonFile(genshinFolderPath + "/data/categories.json");
        if (categoriesObject == null) return;
        JSONArray dayArray = categoriesObject.getJSONArray("day");
        if (dayArray == null) return;
        dayList = dayArray.toJavaList(String.class);
        JSONArray regionArray = categoriesObject.getJSONArray("region");
        if (regionArray == null) return;
        regionList = regionArray.toJavaList(String.class);
        JSONArray rarityArray = categoriesObject.getJSONArray("rarity");
        if (rarityArray == null) return;
        rarityList = rarityArray.toJavaList(String.class);
        categoriesObject.clear(); // paranoia

        // 获取天赋材料汇总初始数据
        JSONObject tmtObject = BotUtil.readJsonFile(genshinFolderPath + "/index/talentmaterialtypes.json");
        if (tmtObject == null) return;
        JSONObject tmtCategories = tmtObject.getJSONObject("categories");
        if (tmtCategories == null) return;
        tmtObject.clear(); // paranoia

        // 获取武器材料汇总初始数据
        JSONObject wmtObject = BotUtil.readJsonFile(genshinFolderPath + "/index/weaponmaterialtypes.json");
        if (wmtObject == null) return;
        JSONObject wmtCategories = wmtObject.getJSONObject("categories");
        if (wmtCategories == null) return;
        wmtObject.clear(); // paranoia

        // 根据星期数组生成天赋材料字典
        for (String day : dayList) {
            JSONArray tmtArray = tmtCategories.getJSONArray(day);
            if (tmtArray == null) continue;
            List<TalentMaterialTypeBean> tmtList = new ArrayList<>();
            for (int j = 0; j < tmtArray.size(); j++) {
                TalentMaterialTypeBean tmtBean = getTalentMaterialTypeBean(tmtArray.getString(j));
                if (tmtBean != null) tmtList.add(tmtBean);
            }
            tmtMap.put(day, tmtList);
        }
        tmtCategories.clear(); // paranoia

        // 根据星期数组生成武器材料字典
        for (String day : dayList) {
            JSONArray wmtArray = wmtCategories.getJSONArray(day);
            List<WeaponMaterialTypeBean> wmtList = new ArrayList<>();
            for (int j = 0; j < wmtArray.size(); j++) {
                WeaponMaterialTypeBean wmtBean = getWeaponMaterialTypeBean(wmtArray.getString(j));
                if (wmtBean != null) wmtList.add(wmtBean);
            }
            wmtMap.put(day, wmtList);
        }
        wmtCategories.clear(); // paranoia

        // 获取角色初始数据
        JSONObject charObject = BotUtil.readJsonFile(genshinFolderPath + "/index/characters.json");
        if (charObject == null) return;
        JSONObject charCategories = wmtObject.getJSONObject("categories");
        if (charCategories == null) return;
        // 根据星期数组生成字典
        for (String rarity : rarityList) {
            JSONArray charArray = charCategories.getJSONArray(rarity);
            if (charArray == null) continue;
            List<TalentBean> talentList = new ArrayList<>();
            for (int j = 0; j < charArray.size(); j++) {
                TalentBean talentBean = getTalentBean(charArray.getString(j));
                if (talentBean != null) talentList.add(talentBean);
            }
            talentBeanList.put(rarity, talentList);
        }

        //TODO: 获取武器初始数据

        // 生成图片
        for (int i = 0; i < dayList.size(); i++) { // 按日期
            for (int j = 0; j < regionList.size(); j++) { // 按国家
                //TODO: 角色

                //TODO: 武器
            }
        }
    }

    /**
     * 获取天赋材料对象
     * @param fileName 文件名
     * @return 天赋材料对象
     */
    private TalentMaterialTypeBean getTalentMaterialTypeBean(String fileName) {
        String filePath = BotUtil.getGenshinFolderPath() + "/data/talentmaterialtypes/" + fileName;
        JSONObject object = BotUtil.readJsonFile(filePath);
        if (object != null) {
            return new TalentMaterialTypeBean(
                    object.getString("name"),
                    object.getString("2starname"),
                    object.getString("3starname"),
                    object.getString("4starname"),
                    object.getJSONArray("day").toJavaList(String.class),
                    object.getString("location"),
                    object.getString("region"),
                    object.getString("domainofmastery")
            );
        }
        return null;
    }

    /**
     * 获取武器材料对象
     * @param fileName 文件名
     * @return 武器材料对象
     */
    private WeaponMaterialTypeBean getWeaponMaterialTypeBean(String fileName) {
        String filePath = BotUtil.getGenshinFolderPath() + "/data/weaponmaterialtypes/" + fileName;
        JSONObject object = BotUtil.readJsonFile(filePath);
        if (object != null) {
            return new WeaponMaterialTypeBean(
                    object.getString("name"),
                    object.getString("2starname"),
                    object.getString("3starname"),
                    object.getString("4starname"),
                    object.getString("5starname"),
                    object.getJSONArray("day").toJavaList(String.class),
                    object.getString("location"),
                    object.getString("region"),
                    object.getString("domainofmastery")
            );
        }
        return null;
    }

    /**
     * 获取天赋对象
     * @param fileName
     * @return
     */
    private TalentBean getTalentBean(String fileName) {
        String filePath = BotUtil.getGenshinFolderPath() + "/data/talents/" + fileName;
        JSONObject object = BotUtil.readJsonFile(filePath);
        if (object != null) {
            // 天赋升级材料消耗
            Map<String, List<CostBean>> costMap = new HashMap<>();
            JSONObject costs = object.getJSONObject("costs");
            if (costs != null) {
                for (String key : costs.keySet()) {
                    List<CostBean> levelList = new ArrayList<>();
                    JSONArray level = costs.getJSONArray(key);
                    for (int i = 0; i < level.size(); i++) {
                        JSONObject cost = level.getJSONObject(i);
                        if (cost != null) {
                            CostBean bean = new CostBean(
                                    cost.getString("name"),
                                    cost.getIntValue("count")
                            );
                            levelList.add(bean);
                        }
                    }
                    costMap.put(key, levelList);
                }
            }

            //TODO: 当前只关心消耗材料，后续将补充完整
            return new TalentBean(
                    object.getString("name"),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    costMap
            );
        }
        return null;
    }
}
