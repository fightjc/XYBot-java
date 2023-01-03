package org.fightjc.xybot.command.impl.friend;

import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;
import org.fightjc.xybot.command.FriendCommand;
import org.fightjc.xybot.enums.ResultCode;
import org.fightjc.xybot.model.dto.ResultOutput;

import java.util.ArrayList;

public abstract class BaseFriendCommand implements FriendCommand {

    @Override
    public Message execute(Friend sender, ArrayList<String> args, MessageChain messageChain, Friend subject) throws Exception {
        ResultOutput<String> result = checkRole(sender, subject);
        if (ResultCode.SUCCESS.getCode() != result.getCode()) {
            return new PlainText(result.getMsg());
        }
        return executeHandle(sender, args, messageChain, subject);
    }

    /**
     * 返回信息
     * @param sender
     * @param args
     * @param messageChain
     * @param subject
     * @return
     * @throws Exception
     */
    protected abstract Message executeHandle(Friend sender, ArrayList<String> args, MessageChain messageChain, Friend subject) throws Exception;

    /**
     * 权限判断
     * @param sender
     * @param subject
     * @return
     */
    protected abstract  ResultOutput<String> checkRole(Friend sender, Friend subject);
}
