package org.fightjc.xybot.events;

import kotlin.coroutines.CoroutineContext;
import net.mamoe.mirai.event.*;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MemberJoinEvent;
import org.fightjc.xybot.util.MessageUtil;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GroupEvents extends SimpleListenerHost {

    private static final Logger logger = LoggerFactory.getLogger(GroupEvents.class);

    @Override
    public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {
        // 处理事件处理时抛出的异常
    }

    /**
     * 加群事件
     * @param event
     * @return
     */
    @NotNull
    @EventHandler(priority = EventPriority.NORMAL)
    public ListeningStatus onMemberJoin(@NotNull MemberJoinEvent event) {
        //String name = event.getMember().getNameCard();
        //event.getGroup().sendMessage("欢迎 " + name);

        return ListeningStatus.LISTENING;
    }

    /**
     * 收到群消息
     * @param event
     * @return
     */
    @NotNull
    @EventHandler(priority = EventPriority.NORMAL)
    public ListeningStatus onReceiveGroupMessage(@NotNull GroupMessageEvent event) {
        //String textMessage = MessageUtil.filterMessage(event.getMessage());
        String text = event.getMessage().contentToString();
        //event.getGroup().getId()
        //event.getSender().getId()
        logger.info(text);

        return ListeningStatus.LISTENING;
    }
}
