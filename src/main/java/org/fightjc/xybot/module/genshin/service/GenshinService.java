package org.fightjc.xybot.module.genshin.service;

import org.fightjc.xybot.model.dto.ResultOutput;
import org.fightjc.xybot.module.genshin.pojo.GroupCalendarBean;
import org.fightjc.xybot.module.genshin.service.impl.GenshinServiceImpl;

import java.awt.image.BufferedImage;
import java.util.List;

public interface GenshinService {

    /**
     * 获取每日素材汇总图片
     * @param day
     * @return
     */
    ResultOutput<BufferedImage> getDailyMaterial(GenshinServiceImpl.DAILY_MATERIAL_WEEK day);

    /**
     * 更新每日素材汇总图片
     * @return
     */
    ResultOutput<String> updateDailyMaterial();

    /**
     * 通过名字查询角色或物品
     * @param name
     * @return
     */
    ResultOutput<BufferedImage> getInfoByName(String name);

    /**
     * 检查原神资源完整性
     * @return
     */
    ResultOutput<String> checkGenshinResource();

    /**
     * 获取原神日历
     * @return
     */
    ResultOutput<BufferedImage> getCalendar();

    /**
     * 获取数据库中所有订阅原神日历记录
     * @return
     */
    List<GroupCalendarBean> getAllGroupCalendar();

    /**
     * 获取数据库中指定群订阅原神日历记录
     * @param groupId
     * @return
     */
    GroupCalendarBean getGroupCalendarByGroupId(Long groupId);

    /**
     * 新增或修改原神日历记录
     * @param groupId
     * @param isActive
     * @param modifiedUserId
     */
    void createOrUpdateGroupCalendar(Long groupId, boolean isActive, Long modifiedUserId);

    /**
     * 向订阅群推送原神日历
     */
    void postGroupGenshinCalendar();
}
