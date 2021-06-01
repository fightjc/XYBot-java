package org.fightjc.xybot.command.impl.friend;

import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;
import org.fightjc.xybot.annotate.CommandAnnotate;
import org.fightjc.xybot.pojo.Command;
import org.fightjc.xybot.util.BotSwitch;

import java.util.ArrayList;

@CommandAnnotate
public class SwitchCommand extends AdminFriendCommand {

    @Override
    public Command property() {
        return new Command("开关");
    }

    @Override
    protected Message executeHandle(Friend sender, ArrayList<String> args, MessageChain messageChain, Friend subject) throws Exception {
        if (args.size() != 2) {
            return new PlainText("使用方式：开关 [开启/关闭] [组件名]\n当前组件有：\n" + BotSwitch.getList());
        }

        String opt = args.get(0);
        String componentName = args.get(1);

        switch (opt) {
            case "开启":
                return new PlainText(BotSwitch.open(componentName).getInfo());
            case "关闭":
                return new PlainText(BotSwitch.close(componentName).getInfo());
            default:
                return new PlainText("使用方式：开关 [开启/关闭] [组件名]\n当前组件有：\n" + BotSwitch.getList());
        }
    }
}
