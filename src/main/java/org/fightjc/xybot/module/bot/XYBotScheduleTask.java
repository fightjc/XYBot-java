package org.fightjc.xybot.module.bot;

import org.fightjc.xybot.module.bilibili.service.BiliBiliService;
import org.fightjc.xybot.module.genshin.service.GenshinService;
import org.fightjc.xybot.util.BotGacha;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class XYBotScheduleTask {

    private static final Logger logger = LoggerFactory.getLogger(XYBotScheduleTask.class);

    @Value("${mode.debug}")
    boolean isDebugMode;

    @Autowired
    protected BiliBiliService biliBiliService;

    @Autowired
    protected GenshinService genshinService;

    /**
     * 每天4时重置抽签记录
     */
    @Scheduled(cron = "0 0 4 * * ?")
    private void resetGacha() {
        logger.info("重置每日抽签记录");
        BotGacha.getInstance().clearRecord();
    }

    /**
     * 每隔5分钟查看一次b站订阅
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    private void biliBiliSubscribe() {
        if (!isDebugMode) {
            logger.info("查看b站订阅");
            biliBiliService.checkNeedGroupNotify();
        }
    }

    /**
     * 每天9时推送原神日历
     */
    @Scheduled(cron = "0 0 9 * * ?")
    private void genshinCalendar() {
        logger.info("推送原神日历");
        genshinService.postGroupGenshinCalendar();
    }
}
