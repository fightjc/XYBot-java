package org.fightjc.xybot.events;

import kotlin.coroutines.CoroutineContext;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.EventPriority;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.Message;
import org.apache.commons.lang3.StringUtils;
import org.fightjc.xybot.annotate.SwitchAnnotate;
import org.fightjc.xybot.command.BaseCommand;
import org.fightjc.xybot.command.FriendCommand;
import org.fightjc.xybot.command.GroupCommand;
import org.fightjc.xybot.util.BotSwitch;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 指令系统
 */
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
    private final Map<String, BaseCommand> friendCommands = new HashMap<>();

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

        if (command instanceof FriendCommand) {
            friendCommands.putAll(tempList);
        }

        if (command instanceof GroupCommand) {
            groupCommands.putAll(tempList);
        }
    }

    /**
     * 收到群消息
     * @param event
     * @return
     */
    @NotNull
    @EventHandler(priority = EventPriority.NORMAL)
    public ListeningStatus onReceiveGroupMessage(@NotNull GroupMessageEvent event) throws Exception {
        String rawMessage = event.getMessage().contentToString();
        // 判断是否是指令
        if (isCommand(rawMessage)) {
            GroupCommand groupCommand = (GroupCommand) getCommand(rawMessage, groupCommands);
            // 获取对应处理群消息的指令
            if (groupCommand != null) {
                SwitchAnnotate annotate = groupCommand.getClass().getAnnotation(SwitchAnnotate.class);
                // 如果是动态组件指令则查询是否有开启该指令
                if (annotate != null && !BotSwitch.check(annotate.name())) {
                    return ListeningStatus.LISTENING;
                }
                // 执行指令
                Message result = groupCommand.execute(event.getSender(), getArgs(rawMessage), event.getMessage(), event.getSubject());
                if (result != null) {
                    // 若有返回结果则发送消息
                    event.getSubject().sendMessage(result);
                }
                // 事件拦截
                event.intercept();
            }
        }

        return ListeningStatus.LISTENING;
    }

    /**
     * 收到私聊消息
     * @param event
     * @return
     * @throws Exception
     */
    @NotNull
    @EventHandler(priority = EventPriority.NORMAL)
    public ListeningStatus onReceiveFriendMessage(@NotNull FriendMessageEvent event) throws Exception {
        String rawMessage = event.getMessage().contentToString();
        // 判断是否是指令
        if (isCommand(rawMessage)) {
            FriendCommand friendCommand = (FriendCommand) getCommand(rawMessage, friendCommands);
            if (friendCommand != null) {
                SwitchAnnotate annotate = friendCommand.getClass().getAnnotation(SwitchAnnotate.class);
                // 如果是动态组件指令则查询是否有开启该指令
                if (annotate != null && !BotSwitch.check(annotate.name())) {
                    return ListeningStatus.LISTENING;
                }
                // 执行指令
                Message result = friendCommand.execute(event.getSender(), getArgs(rawMessage), event.getMessage(), event.getSubject());
                if (result != null) {
                    // 若有返回结果则发送消息
                    event.getSubject().sendMessage(result);
                }
                // 事件拦截
                event.intercept();
            }
        }

        return ListeningStatus.LISTENING;
    }

    @Override
    public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {
        super.handleException(context, exception);
        logger.error("CommandEvents", exception.getMessage());
    }

    /**
     * 判断是否带有指令头
     * @return
     */
    private boolean isCommand(String msg) {
        return commandHeaders.stream().anyMatch(msg::startsWith);
    }

    /**
     * 获取指令
     * @return
     */
    private BaseCommand getCommand(String msg, Map<String, BaseCommand> commandMap) {
        String[] args = msg.split(" ");
        String header = args[0];

        List<String> temps = commandHeaders.stream()
                .filter(head -> StringUtils.isNotBlank(head) && header.startsWith(head))
                .map(head -> header.replaceFirst(head, ""))
                .collect(Collectors.toList());

        String commandString;
        if (temps.isEmpty()) {
            commandString = header;
        } else {
            commandString = temps.get(0);
        }

        return commandMap.getOrDefault(commandString.toLowerCase(), null);
    }

    /**
     * 获取当前指令的所有空格分割的参数
     * @param msg
     * @return
     */
    private ArrayList<String> getArgs(String msg) {
        String[] args = msg.trim().split(" ");
        ArrayList<String> list = new ArrayList<>();
        for (String arg : args) {
            if (StringUtils.isNotBlank(arg)) {
                list.add(arg);
            }
        }
        // 去掉指令本身
        list.remove(0);
        return list;
    }
}
