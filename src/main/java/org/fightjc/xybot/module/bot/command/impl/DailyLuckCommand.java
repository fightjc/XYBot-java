package org.fightjc.xybot.module.bot.command.impl;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;
import org.fightjc.xybot.module.bot.annotate.CommandAnnotate;
import org.fightjc.xybot.module.bot.annotate.SwitchAnnotate;
import org.fightjc.xybot.module.bot.command.impl.group.MemberGroupCommand;
import org.fightjc.xybot.model.Command;
import org.fightjc.xybot.model.dto.ResultOutput;
import org.fightjc.xybot.util.BotGacha;

import java.util.ArrayList;

@CommandAnnotate
@SwitchAnnotate(name = "每日抽签")
public class DailyLuckCommand extends MemberGroupCommand {

    public DailyLuckCommand() {
        // 加载抽签内容
        BotGacha.getInstance().reloadItems();
    }

    @Override
    public Command property() {
        return new Command("抽签");
    }

    @Override
    protected Message executeHandle(Member sender, ArrayList<String> args, MessageChain messageChain, Group subject) throws Exception {
        ResultOutput<String> result = BotGacha.getInstance().getGacha(sender.getId());

        At at = new At(sender.getId());
        PlainText plainText = new PlainText(result.getMsg());
        return at.plus(plainText);
    }
}
