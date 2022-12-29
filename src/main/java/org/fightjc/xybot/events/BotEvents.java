package org.fightjc.xybot.events;

import kotlin.coroutines.CoroutineContext;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.EventPriority;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.NudgeEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BotEvents extends SimpleListenerHost {

    private static final Logger logger = LoggerFactory.getLogger(BotEvents.class);

    @Value("${bot.admin}")
    protected Long adminUid;

    @Override
    public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {
        super.handleException(context, exception);
        logger.error(exception.getMessage());
    }

//    /**
//     * 机器人被戳
//     * @param event
//     * @return
//     * @throws Exception
//     */
//    @NotNull
//    @EventHandler(priority = EventPriority.NORMAL)
//    public ListeningStatus onBotNudged(@NotNull NudgeEvent event) throws Exception {
//        return ListeningStatus.LISTENING;
//    }

//    /**
//     * Bot 登录完成
//     * @param event
//     * @return
//     * @throws Exception
//     */
//    public ListeningStatus onBotOnlineEvent(@NotNull BotOnlineEvent event) throws Exception {
//        event.getBot().getFriend(adminUid).sendMessage("机器人登录成功");
//
//        return ListeningStatus.LISTENING;
//    }
}
