package org.fightjc.xybot.service;

import org.fightjc.xybot.pojo.ResultOutput;
import org.fightjc.xybot.service.impl.GenshinServiceImpl;

import java.awt.image.BufferedImage;

public interface GenshinService {

    /**
     * 获取每日角色天赋突破材料汇总图
     * @param day
     * @return
     */
    ResultOutput<BufferedImage> getDailyMaterial(GenshinServiceImpl.DAILY_MATERIAL_WEEK day);
}
