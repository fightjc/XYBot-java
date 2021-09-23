package org.fightjc.xybot.command.impl;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import org.fightjc.xybot.annotate.CommandAnnotate;
import org.fightjc.xybot.annotate.SwitchAnnotate;
import org.fightjc.xybot.command.impl.group.AdminGroupCommand;
import org.fightjc.xybot.pojo.Command;

import java.util.ArrayList;

@CommandAnnotate
@SwitchAnnotate(name = "b站订阅")
public class BiliBiliCommand extends AdminGroupCommand {

    @Override
    public Command property() {
        return new Command("b站");
    }

    @Override
    protected Message executeHandle(Member sender, ArrayList<String> args, MessageChain messageChain, Group subject) throws Exception {
        return null;
    }
}
