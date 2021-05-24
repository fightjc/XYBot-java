package org.fightjc.xybot.command.impl.group;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import org.fightjc.xybot.pojo.ResultOutput;

public abstract class MemberGroupCommand extends BaseGroupCommand {

    @Override
    protected ResultOutput<String> checkRole(Member sender, Group subject) {
        return new ResultOutput<>(true, "role permitted");
    }
}
