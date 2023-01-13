package org.fightjc.xybot.module.bot.command.impl;

import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;
import org.fightjc.xybot.module.bot.annotate.CommandAnnotate;
import org.fightjc.xybot.module.bot.command.impl.friend.AdminFriendCommand;
import org.fightjc.xybot.model.Command;
import org.fightjc.xybot.util.BotGacha;

import java.util.ArrayList;
import java.util.List;

@CommandAnnotate
public class MaintenanceCommand extends AdminFriendCommand {

    @Override
    public Command property() {
        return new Command("维护");
    }

    @Override
    protected Message executeHandle(Friend sender, ArrayList<String> args, MessageChain messageChain, Friend subject) throws Exception {
        String usage = "使用方式：维护 [子功能] [子操作]\n\n" +
                        "抽签 [重置]";

        List<String> subFunctions = new ArrayList<String>() {{
            add("抽签");
        }};

        if (args.size() == 0) {
            return new PlainText(usage);
        }

        String subFunc = args.get(0);
        switch (subFunc) {
            case "抽签":
                if (args.size() == 1) {
                    return new PlainText("使用方式：维护 抽签 [重置]");
                }
                String opt = args.get(1);
                if ("重置".equals(opt)) {
                    BotGacha.getInstance().reloadItems();
                    return new PlainText("重置抽签完成！");
                }

                return new PlainText("使用方式：维护 抽签 [重置]");
            default:
                return new PlainText(usage);
        }
    }
}
