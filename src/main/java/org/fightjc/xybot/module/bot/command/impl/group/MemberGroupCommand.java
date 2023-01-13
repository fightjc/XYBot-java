package org.fightjc.xybot.module.bot.command.impl.group;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import org.fightjc.xybot.enums.ResultCode;
import org.fightjc.xybot.model.dto.ResultOutput;

public abstract class MemberGroupCommand extends BaseGroupCommand {

    @Override
    protected ResultOutput<String> checkRole(Member sender, Group subject) {
        return new ResultOutput<>(ResultCode.SUCCESS, "role permitted");
    }
}
