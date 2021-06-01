package org.fightjc.xybot.command.impl.friend;

import net.mamoe.mirai.contact.Friend;
import org.fightjc.xybot.pojo.ResultOutput;
import org.springframework.beans.factory.annotation.Value;

public abstract class AdminFriendCommand extends BaseFriendCommand {

    @Value("${bot.admin}")
    protected Long adminUid;

    @Override
    protected ResultOutput<String> checkRole(Friend sender, Friend subject) {
        if (sender.getId() == adminUid) {
            return new ResultOutput<>(true, "role permitted");
        } else {
            return new ResultOutput<>(false, "无权限操作该指令，请联系管理员");
        }
    }
}
