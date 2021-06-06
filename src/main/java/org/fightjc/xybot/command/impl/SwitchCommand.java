package org.fightjc.xybot.command.impl;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;
import org.fightjc.xybot.annotate.CommandAnnotate;
import org.fightjc.xybot.command.impl.group.AdminGroupCommand;
import org.fightjc.xybot.pojo.Command;
import org.fightjc.xybot.util.BotSwitch;

import java.util.ArrayList;

@CommandAnnotate
public class SwitchCommand extends AdminGroupCommand {

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

        if (opt.equals("列表")) {
            return new PlainText("当前组件有：\n" + BotSwitch.getList());
        }

        String componentName = args.get(1);
        switch (opt) {
            case "开启":
                return new PlainText(BotSwitch.open(componentName).getInfo());
            case "关闭":
                return new PlainText(BotSwitch.close(componentName).getInfo());
            default:
                return new PlainText("使用方式：开关 [列表] [开启/关闭 组件名]");
        }
    }
}
