package org.fightjc.xybot.command.impl;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;
import org.fightjc.xybot.annotate.CommandAnnotate;
import org.fightjc.xybot.command.impl.group.AdminGroupCommand;
import org.fightjc.xybot.pojo.Command;
import org.fightjc.xybot.pojo.GroupSwitch;
import org.fightjc.xybot.pojo.ResultOutput;
import org.fightjc.xybot.service.GroupSwitchService;
import org.fightjc.xybot.util.BotSwitch;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CommandAnnotate
public class SwitchCommand extends AdminGroupCommand {

    @Autowired
    protected GroupSwitchService groupSwitchService;

    @Override
    public Command property() {
        return new Command("开关");
    }

    @Override
    protected Message executeHandle(Member sender, ArrayList<String> args, MessageChain messageChain, Group subject) throws Exception {
        if (args.size() <= 0 || 2 < args.size()) {
            return new PlainText("使用方式：开关 [列表] [开启/关闭 组件名]");
        }

        String opt = args.get(0);
        switch (opt) {
            case "列表":
                // 获取默认功能开关状态
                Map<String, Boolean> switchList = BotSwitch.getInstance().getSwitchList();
                // 获取对应数据库记录
                List<GroupSwitch> groupSwitchList = groupSwitchService.getGroupSwitchesByGroupId(subject.getId());

                StringBuilder message = new StringBuilder();
                for (String key : switchList.keySet()) {
                    // 查看是否已有记录
                    GroupSwitch groupSwitch = groupSwitchList.stream()
                            .filter(gs -> gs.getName().equals(key))
                            .findFirst()
                            .orElse(null);
                    if (groupSwitch == null) {
                        message.append(key).append(" ").append(switchList.getOrDefault(key, false) ? "开启中" : "未开启").append("\n");
                    } else {
                        message.append(groupSwitch.getName()).append(" ").append(groupSwitch.isOn() ? "开启中" : "未开启").append("\n");
                    }
                }

                return new PlainText("当前组件有：\n" + message.toString());
            case "开启":
            case "关闭":
                String componentName = args.get(1);

                // 检测功能是否存在
                ResultOutput<Boolean> result = BotSwitch.getInstance().getSwitchDefaultValue(componentName);
                if (!result.getSuccess()) {
                    return new PlainText(result.getInfo());
                }

                // 写入数据库
                groupSwitchService.createOrUpdateGroupSwitch(subject.getId(), componentName, opt.equals("开启"), sender.getId());
                // 更新工具类
                BotSwitch.getInstance().createOrUpdateGroupSwitch(subject.getId(), componentName, opt.equals("开启"));

                String info = "[" + componentName + "] " + (opt.equals("开启") ? "已开启" : "已关闭");
                return new PlainText(info);
            default:
                return new PlainText("使用方式：开关 [列表] [开启/关闭 组件名]");
        }
    }
}
