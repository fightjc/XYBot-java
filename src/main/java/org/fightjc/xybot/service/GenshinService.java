package org.fightjc.xybot.service;

import org.fightjc.xybot.pojo.ResultOutput;
import org.fightjc.xybot.service.impl.GenshinServiceImpl;

import java.awt.image.BufferedImage;

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
    ResultOutput updateDailyMaterial();

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
    ResultOutput checkGenshinResource();

    /**
     * 获取原神日历
     * @return
     */
    ResultOutput<BufferedImage> getCalendar();
}
