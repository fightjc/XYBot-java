package org.fightjc.xybot.events;

import kotlin.coroutines.CoroutineContext;
import net.mamoe.mirai.event.SimpleListenerHost;
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
