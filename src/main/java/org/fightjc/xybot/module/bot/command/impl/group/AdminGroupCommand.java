package org.fightjc.xybot.module.bot.command.impl.group;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import org.fightjc.xybot.enums.ResultCode;
import org.fightjc.xybot.model.dto.ResultOutput;
import org.springframework.beans.factory.annotation.Value;

public abstract class AdminGroupCommand extends BaseGroupCommand {

    @Value("${bot.admin}")
    protected Long adminUid;

    @Override
    protected ResultOutput<String> checkRole(Member sender, Group subject) {
        if (sender.getId() == adminUid || isGroupOwner(sender)) {
            return new ResultOutput<>(ResultCode.SUCCESS, "role permitted");
        } else {
            return new ResultOutput<>(ResultCode.FAILED, "无权限操作该指令，请联系群主或者超管");
        }
    }
}
