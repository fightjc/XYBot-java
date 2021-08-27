package org.fightjc.xybot.events;

import kotlin.coroutines.CoroutineContext;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.EventPriority;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FriendEvents extends SimpleListenerHost {

    private static final Logger logger = LoggerFactory.getLogger(FriendEvents.class);

    @Override
    public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {
        super.handleException(context, exception);
        logger.error(exception.getMessage());
    }

//    /**
//     * 收到私聊消息
//     * @param event
//     * @return
//     * @throws Exception
//     */
//    @NotNull
//    @EventHandler(priority = EventPriority.NORMAL)
//    public ListeningStatus onReceiveFriendMessage(@NotNull FriendMessageEvent event) throws Exception {
//        return ListeningStatus.LISTENING;
//    }
}
