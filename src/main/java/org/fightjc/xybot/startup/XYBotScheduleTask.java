package org.fightjc.xybot.startup;

import org.fightjc.xybot.util.BotGacha;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class XYBotScheduleTask {

    private static final Logger logger = LoggerFactory.getLogger(XYBotScheduleTask.class);

    /**
     * 每天4时重置抽签记录
     */
    @Scheduled(cron = "0 0 4 * * ?")
    private void resetGacha() {
        logger.info("重置每日抽签记录");
        BotGacha.getInstance().clearRecord();
    }
}
