package org.fightjc.xybot.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.utils.ExternalResource;
import org.fightjc.xybot.bot.XYBot;
import org.fightjc.xybot.dao.GenshinDao;
import org.fightjc.xybot.po.HttpClientResult;
import org.fightjc.xybot.pojo.ResultOutput;
import org.fightjc.xybot.pojo.genshin.*;
import org.fightjc.xybot.service.GenshinService;
import org.fightjc.xybot.util.BotUtil;
import org.fightjc.xybot.util.HttpClientUtil;
import org.fightjc.xybot.util.ImageUtil;
import org.fightjc.xybot.util.MessageUtil;
import org.fightjc.xybot.util.genshin.GenshinAnnounceDrawHelper;
import org.fightjc.xybot.util.genshin.GenshinMaterialDrawHelper;
import org.fightjc.xybot.util.genshin.GenshinSearchDrawHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GenshinServiceImpl implements GenshinService {

    private static final Logger logger = LoggerFactory.getLogger(GenshinServiceImpl.class);

    @Autowired
    public GenshinDao genshinDao;

    //region 数据库操作

    public List<GroupCalendarBean> getAllGroupCalendar() {
        List<GroupCalendarBean> groupCalendarBeanList = genshinDao.getAllGroupCalendar();
        // 防止队列中有空结果
        groupCalendarBeanList.removeIf(Objects::isNull);

        return groupCalendarBeanList;
    }

    public GroupCalendarBean getGroupCalendarByGroupId(Long groupId) {
        return genshinDao.getGroupCalendar(groupId);
    }

    public void createOrUpdateGroupCalendar(Long groupId, boolean isActive, Long modifiedUserId) {
        GroupCalendarBean groupCalendarBean = genshinDao.getGroupCalendar(groupId);
        if (groupCalendarBean == null) {
            createGroupCalendar(groupId, isActive, modifiedUserId);
        } else {
            updateGroupCalendar(groupId, isActive, modifiedUserId);
        }
    }

    private void createGroupCalendar(Long groupId, boolean isActive, Long modifiedUserId) {
        GroupCalendarBean groupCalendarBean = new GroupCalendarBean(groupId, isActive);
        genshinDao.createGroupCalendar(groupCalendarBean);
        GroupCalendarRecordBean groupCalendarRecordBean = new GroupCalendarRecordBean(groupId, isActive,
                modifiedUserId, MessageUtil.getCurrentDateTime());
        genshinDao.createGroupCalendarRecord(groupCalendarRecordBean);
    }

    private void updateGroupCalendar(Long groupId, boolean isActive, Long modifiedUserId) {
        GroupCalendarBean groupCalendarBean = new GroupCalendarBean(groupId, isActive);
        genshinDao.updateGroupCalendar(groupCalendarBean);
        GroupCalendarRecordBean groupCalendarRecordBean = new GroupCalendarRecordBean(groupId, isActive,
                modifiedUserId, MessageUtil.getCurrentDateTime());
        genshinDao.createGroupCalendarRecord(groupCalendarRecordBean);
    }

    //endregion

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
            return getCharacterInfo(characterNameMap.get(name));
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
        ResultOutput<List<AnnounceBean>> result = getGenshinAnnouncement();
        if (result.getSuccess()) {
            List<AnnounceBean> announceList = result.getObject();
            BufferedImage bi = GenshinAnnounceDrawHelper.drawAnnounce(announceList);
            return new ResultOutput<>(true, "", bi);
        }
        return new ResultOutput<>(false, result.getInfo());
    }

    /**
     * 向订阅群推送原神日历
     */
    public void postGroupGenshinCalendar() {
        ResultOutput<BufferedImage> result = getCalendar();
        if (result.getSuccess()) {
            BufferedImage image = result.getObject();
            if (image != null) {
                try {
                    ExternalResource resource = ImageUtil.bufferedImage2ExternalResource(image);

                    List<GroupCalendarBean> groupCalendarBeanList = getAllGroupCalendar();
                    groupCalendarBeanList.removeIf(bean -> !bean.isActive()); // 过滤非激活的

                    // 推送群
                    for (GroupCalendarBean bean : groupCalendarBeanList) {
                        Group group = XYBot.getBot().getGroup(bean.getGroupId());
                        if (group != null) {
                            group.sendMessage(group.uploadImage(resource));
                        }
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        }

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

        // 角色图标
        String imagePath = BotUtil.getGenshinFolderPath() + "/images/characters/" + name + ".png";
        BufferedImage image = BotUtil.readImageFile(imagePath);
        if (image == null) {
            logger.error("获取 /images/characters/" + name + ".png 对象失败");
            return new ResultOutput<>(false, "获取 /images/characters/" + name + ".png 对象失败");
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
        if (false) {
            ConstellationBean constellationBean = BotUtil.readJsonFile(
                    BotUtil.getGenshinFolderPath() + "/data/constellations/" + name + ".json",
                    ConstellationBean.class);
            if (constellationBean == null) {
                logger.error("获取 /data/constellations/" + name + ".json 对象失败");
                return new ResultOutput<>(false, "获取 /data/constellations/" + name + ".json 对象失败");
            }
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

        CostDto mora;
        List<CostDto> avatarList = new ArrayList<>();
        List<CostDto> exchangeList = new ArrayList<>();
        CostDto talent_mora;
        List<CostDto> talent_avatarList = new ArrayList<>();
        List<CostDto> talentList = new ArrayList<>();

        // 角色突破
        {
            Map<String, MaterialBean> materialMap = new HashMap<>(); // 存储当前材料详细信息
            Map<String, Map<String, Integer>> totalMaterialMap = new HashMap<>(); // 以材料类别和材料名分类
            Map<String, List<CostBean>> costMap = characterBean.getCosts();
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
                    String type = material.getCategory();
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
            Map<String, Integer> adsorbateMap = totalMaterialMap.get("ADSORBATE");
            mora = new CostDto("摩拉", adsorbateMap.get("摩拉"),
                    BotUtil.readImageFile(BotUtil.getGenshinFolderPath() + "/images/materials/mora.png"),
                    0, "");
            Map<String, Integer> avatarMap = totalMaterialMap.get("AVATAR_MATERIAL");
            for (String key : avatarMap.keySet()) {
                String materialFilePath = materialNameMap.get(key);
                CostDto cost = new CostDto(key, avatarMap.get(key),
                        BotUtil.readImageFile(BotUtil.getGenshinFolderPath() + "/images/materials/" + materialFilePath + ".png"),
                        materialMap.get(key).getSortOrder(), "");
                avatarList.add(cost);
            }
            avatarList.sort(Comparator.comparing(CostDto::getSort)); // 根据sortOrder排序
            Map<String, Integer> exchangeMap = totalMaterialMap.get("EXCHANGE");
            for (String key : exchangeMap.keySet()) {
                String materialFilePath = materialNameMap.get(key);
                MaterialBean materialBean = materialMap.get(key);
                List<String> daysOfWeek = materialBean.getDaysOfWeek();
                String info = daysOfWeek == null ?
                        "" : "（" + String.join("/", materialBean.getDaysOfWeek()) + "）"; // 刷取日期
                CostDto cost = new CostDto(key, exchangeMap.get(key),
                        BotUtil.readImageFile(BotUtil.getGenshinFolderPath() + "/images/materials/" + materialFilePath + ".png"),
                        materialBean.getSortOrder(), info);
                exchangeList.add(cost);
            }
            exchangeList.sort(Comparator.comparing(CostDto::getSort)); // 根据sortOrder排序
        }

        // 天赋突破
        {
            Map<String, MaterialBean> materialMap = new HashMap<>(); // 存储当前材料详细信息
            Map<String, Map<String, Integer>> totalMaterialMap = new HashMap<>(); // 以材料类别和材料名分类
            Map<String, List<CostBean>> costMap = talentBean.getCosts();
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
            Map<String, Integer> adsorbateMap = totalMaterialMap.get("通用货币");
            talent_mora = new CostDto("摩拉", adsorbateMap.get("摩拉"),
                    BotUtil.readImageFile(BotUtil.getGenshinFolderPath() + "/images/materials/mora.png"),
                    0, "");
            Map<String, Integer> avatarMap = totalMaterialMap.get("角色培养素材");
            for (String key : avatarMap.keySet()) {
                String materialFilePath = materialNameMap.get(key);
                CostDto cost = new CostDto(key, avatarMap.get(key),
                        BotUtil.readImageFile(BotUtil.getGenshinFolderPath() + "/images/materials/" + materialFilePath + ".png"),
                        materialMap.get(key).getSortOrder(), "");
                talent_avatarList.add(cost);
            }
            talent_avatarList.sort(Comparator.comparing(CostDto::getSort)); // 根据sortOrder排序
            Map<String, Integer> talentMap = totalMaterialMap.get("天赋培养素材");
            for (String key : talentMap.keySet()) {
                String materialFilePath = materialNameMap.get(key);
                MaterialBean materialBean = materialMap.get(key);
                List<String> daysOfWeek = materialBean.getDaysOfWeek();
                String info = daysOfWeek == null ?
                        "" : "（" + String.join("/", materialBean.getDaysOfWeek()) + "）"; // 刷取日期
                CostDto cost = new CostDto(key, talentMap.get(key),
                        BotUtil.readImageFile(BotUtil.getGenshinFolderPath() + "/images/materials/" + materialFilePath + ".png"),
                        materialBean.getSortOrder(), info);
                talentList.add(cost);
            }
            talentList.sort(Comparator.comparing(CostDto::getSort)); // 根据sortOrder排序
        }

        BufferedImage target = GenshinSearchDrawHelper.drawCharacterInfo(new CharacterDrawDto(image, characterBean,
                mora, avatarList, exchangeList, talent_mora, talent_avatarList, talentList));
        return new ResultOutput<>(true, "", target);
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
        Map<String, Integer> adsorbateMap = totalMaterialMap.get("通用货币");
        CostDto mora = new CostDto("摩拉", adsorbateMap.get("摩拉"),
                BotUtil.readImageFile(BotUtil.getGenshinFolderPath() + "/images/materials/mora.png"),
                0, "");

        Map<String, Integer> avatarMap = totalMaterialMap.get("角色培养素材");
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
    private ResultOutput<List<AnnounceBean>> getGenshinAnnouncement() {
        List<Integer> ignoreAnnId = Arrays.asList(
                495,  // 有奖问卷调查开启！
                1263, // 米游社《原神》专属工具一览
                423,  // 《原神》玩家社区一览
                422,  // 《原神》防沉迷系统说明
                762   // 《原神》公平运营声明
        );
        List<String> ignoreKeyword = Arrays.asList(
                "修复", "版本内容专题页", "米游社", "调研", "防沉迷"
        );

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

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        List<AnnounceBean> announceList = new ArrayList<>();
        try {
            HttpClientResult httpClientResult = HttpClientUtil.doGet(url, null, params);
            JSONObject content = JSONObject.parseObject(httpClientResult.content);

            int retCode = content.getIntValue("retcode");
            if (retCode == 0) {
                // 成功
                JSONObject data = content.getJSONObject("data");
                JSONArray dataList = data.getJSONArray("list");
                for (int i = 0; i < dataList.size(); i++) {
                    JSONObject list = dataList.getJSONObject(i);
                    JSONArray itemList = list.getJSONArray("list");
                    for (int j = 0; j < itemList.size(); j++) {
                        JSONObject item = itemList.getJSONObject(j);
                        int type = item.getIntValue("type");
                        String title = item.getString("title");

                        // 筛选 1 活动公告 2 游戏公告
                        if (type == 2) {
                            int annId = item.getIntValue("ann_id");
                            if (ignoreAnnId.contains(annId)) {
                                continue;
                            }
                            if (ignoreKeyword.stream().anyMatch(title::contains)) {
                                continue;
                            }
                        }

                        Date startTime = dateFormat.parse(item.getString("start_time"));
                        Date endTime = dateFormat.parse(item.getString("end_time"));

                        // 判断是否永久
                        boolean isForever = title.contains("任务");

                        // 分类封装
                        String tag = item.getString("tag_label");
                        AnnounceBean.AnnounceType announceType = AnnounceBean.AnnounceType.other;
                        if (type == 1) {
                            announceType = AnnounceBean.AnnounceType.event;
                        }
                        if (tag.contains("扭蛋")) {
                            announceType = AnnounceBean.AnnounceType.gacha;
                        }
                        if (title.contains("倍")) {
                            announceType = AnnounceBean.AnnounceType.award;
                        }
                        announceList.add(new AnnounceBean(announceType, title, startTime, endTime, isForever));
                    }
                }
            }

            // 添加深渊提醒
            DateFormat df1 = new SimpleDateFormat("yyyy-MM-01 04:00:00");
            DateFormat df2 = new SimpleDateFormat("yyyy-MM-16 03:59:59");
            DateFormat df3 = new SimpleDateFormat("yyyy-MM-16 04:00:00");
            DateFormat df4 = new SimpleDateFormat("yyyy-MM-01 03:59:59");

            Date now = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, 1);
            Date nextMonth = calendar.getTime();

            if (calendar.get(Calendar.DAY_OF_MONTH) <= 16) {
                // 月初
                announceList.add(new AnnounceBean(AnnounceBean.AnnounceType.abyss, "「深境螺旋」",
                        dateFormat.parse(df1.format(now)), dateFormat.parse(df2.format(now)), false));
                announceList.add(new AnnounceBean(AnnounceBean.AnnounceType.abyss, "「深境螺旋」",
                        dateFormat.parse(df3.format(now)), dateFormat.parse(df4.format(nextMonth)), false));
            } else {
                //月末
                announceList.add(new AnnounceBean(AnnounceBean.AnnounceType.abyss, "「深境螺旋」",
                        dateFormat.parse(df3.format(now)), dateFormat.parse(df4.format(nextMonth)), false));
                announceList.add(new AnnounceBean(AnnounceBean.AnnounceType.abyss, "「深境螺旋」",
                        dateFormat.parse(df1.format(nextMonth)), dateFormat.parse(df2.format(nextMonth)), false));
            }

            // 分别按结束时间和开始时间排序
            announceList.sort(Comparator.comparing((AnnounceBean::getDeadLine)).thenComparing(AnnounceBean::getStartTime));

            return new ResultOutput<>(true, "查询成功", announceList);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultOutput<>(false, "请求网络失败");
        }
    }
}
