package org.fightjc.xybot.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.fightjc.xybot.pojo.Gacha;
import org.fightjc.xybot.pojo.ResultOutput;
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
        List<String> dayList = new ArrayList<>(); // 星期数组
        Map<String, List<TalentMaterialTypeBean>> tmtMap = new HashMap<>(); // 天赋材料字典
        Map<String, List<WeaponMaterialTypeBean>> wmtMap = new HashMap<>(); // 武器材料字典

        // 获取星期数组
        JSONObject categoriesObject = BotUtil.readJsonFile(BotUtil.getGenshinFolderPath() + "/data/categories.json");
        if (categoriesObject == null) return;
        JSONArray dayArray = categoriesObject.getJSONArray("day");
        for (int i = 0; i < dayArray.size(); i++) {
            dayList.add(dayArray.getString(i));
        }
        categoriesObject.clear(); // paranoia

        // 获取天赋材料汇总初始数据
        JSONObject tmtObject = BotUtil.readJsonFile(BotUtil.getGenshinFolderPath() + "/index/talentmaterialtypes.json");
        if (tmtObject == null) return;
        JSONObject tmtCategories = tmtObject.getJSONObject("categories");
        if (tmtCategories == null) return;
        tmtObject.clear(); // paranoia

        // 获取武器材料汇总初始数据
        JSONObject wmtObject = BotUtil.readJsonFile(BotUtil.getGenshinFolderPath() + "/index/weaponmaterialtypes.json");
        if (wmtObject == null) return;
        JSONObject wmtCategories = wmtObject.getJSONObject("categories");
        if (wmtCategories == null) return;
        wmtObject.clear(); // paranoia

        // 根据星期数组生成字典
        for (int i = 0; i < dayList.size(); i++) {
            String day = dayList.get(i);

            // 天赋材料
            JSONArray tmtArray = tmtCategories.getJSONArray(day);
            List<TalentMaterialTypeBean> tmtList = new ArrayList<>();
            for (int j = 0; j < tmtArray.size(); j++) {
                TalentMaterialTypeBean tmtBean = getTalentMaterialTypeBean(tmtArray.getString(j));
                tmtList.add(tmtBean);
            }
            tmtMap.put(day, tmtList);

            // 武器材料
            JSONArray wmtArray = wmtCategories.getJSONArray(day);
            List<WeaponMaterialTypeBean> wmtList = new ArrayList<>();
            for (int j = 0; j < wmtArray.size(); j++) {
                WeaponMaterialTypeBean wmtBean = getWeaponMaterialTypeBean(wmtArray.getString(j));
                wmtList.add(wmtBean);
            }
            wmtMap.put(day, wmtList);
        }
        tmtCategories.clear(); // paranoia
        wmtCategories.clear(); // paranoia

        
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
            JSONArray dayArray = object.getJSONArray("day");
            List<String> dayList = new ArrayList<>();
            for (int i = 0; i < dayArray.size(); i++) {
                dayList.add(dayArray.getString(i));
            }
            TalentMaterialTypeBean bean = new TalentMaterialTypeBean(
                    object.getString("name"),
                    object.getString("2starname"),
                    object.getString("3starname"),
                    object.getString("4starname"),
                    dayList,
                    object.getString("location"),
                    object.getString("region"),
                    object.getString("domainofmastery")
            );
            return bean;
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
            JSONArray dayArray = object.getJSONArray("day");
            List<String> dayList = new ArrayList<>();
            for (int i = 0; i < dayArray.size(); i++) {
                dayList.add(dayArray.getString(i));
            }
            WeaponMaterialTypeBean bean = new WeaponMaterialTypeBean(
                    object.getString("name"),
                    object.getString("2starname"),
                    object.getString("3starname"),
                    object.getString("4starname"),
                    object.getString("5starname"),
                    dayList,
                    object.getString("location"),
                    object.getString("region"),
                    object.getString("domainofmastery")
            );
            return bean;
        }
        return null;
    }
}
