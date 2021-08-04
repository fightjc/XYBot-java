package org.fightjc.xybot.service.impl;

import org.fightjc.xybot.pojo.ResultOutput;
import org.fightjc.xybot.service.GenshinService;
import org.fightjc.xybot.util.BotUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
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
}
