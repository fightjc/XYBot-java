package org.fightjc.xybot.events;

import kotlin.coroutines.CoroutineContext;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.EventPriority;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.MemberJoinEvent;
import net.mamoe.mirai.event.events.MemberLeaveEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GroupEvents extends SimpleListenerHost {

    private static final Logger logger = LoggerFactory.getLogger(GroupEvents.class);

    @Override
    public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {
        super.handleException(context, exception);
        logger.error(exception.getMessage());
    }

    /**
     * 加群事件
     * @param event
     * @return
     */
    @NotNull
    @EventHandler(priority = EventPriority.NORMAL)
    public ListeningStatus onMemberJoin(@NotNull MemberJoinEvent event) throws Exception {
        String name = event.getMember().getNameCard();
        event.getGroup().sendMessage("欢迎新人 " + name);

        return ListeningStatus.LISTENING;
    }

    /**
     * 离群事件
     * @param event
     * @return
     */
    @NotNull
    @EventHandler(priority = EventPriority.NORMAL)
    public ListeningStatus onMemberLeave(@NotNull MemberLeaveEvent event) throws Exception {
        String name = event.getMember().getNameCard();
        event.getGroup().sendMessage("啊，再见了朋友  " + name);

        return ListeningStatus.LISTENING;
    }

//    /**
//     * 收到群消息
//     * @param event
//     * @return
//     */
//    @NotNull
//    @EventHandler(priority = EventPriority.NORMAL)
//    public ListeningStatus onReceiveGroupMessage(@NotNull GroupMessageEvent event) throws Exception {
//        //String textMessage = MessageUtil.filterMessage(event.getMessage());
//        //String text = event.getMessage().contentToString();
//        //event.getGroup().getId()
//        //event.getSender().getId()
//
//        return ListeningStatus.LISTENING;
//    }
}
