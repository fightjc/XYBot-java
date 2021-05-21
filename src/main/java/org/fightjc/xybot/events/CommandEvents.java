package org.fightjc.xybot.events;

import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.EventPriority;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.fightjc.xybot.command.BaseCommand;
import org.fightjc.xybot.pojo.Command;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class CommandEvents extends SimpleListenerHost {

    private static final Logger logger = LoggerFactory.getLogger(CommandEvents.class);

    /**
     * 记录指令头
     */
    private final Set<String> commandHeaders = new HashSet<>();

    /**
     * 注册的指令类型
     */
    private final Map<String, BaseCommand> groupCommands = new HashMap<>();

    /**
     * 注册指令头
     * @param headers
     */
    public void registerCommandHeaders(String ... headers) {
        commandHeaders.addAll(Arrays.asList(headers));
    }

    /**
     * 批量注册指令
     * @param commandList
     */
    public void registerCommands(List<BaseCommand> commandList) {
        for (BaseCommand command : commandList) {
            registerCommand(command);
        }
    }

    /**
     * 注册指令
     * @param command
     */
    private void registerCommand(BaseCommand command) {
        Map<String, BaseCommand> tempList = new HashMap<>();
        tempList.put(command.property().getName().toLowerCase(), command);
        command.property().getAlias().forEach(alias -> tempList.put(alias.toLowerCase(), command));

        switch (command.getClass().toString()) {
            case "GroupCommand":
                groupCommands.putAll(tempList);
                break;
            default:
                // TODO: 默认注册到所有位置
                break;
        }
    }

    /**
     * 收到群消息
     * @param event
     * @return
     */
    @NotNull
    @EventHandler(priority = EventPriority.NORMAL)
    public ListeningStatus onReceiveGroupMessage(@NotNull GroupMessageEvent event) {
        return ListeningStatus.LISTENING;
    }
}
