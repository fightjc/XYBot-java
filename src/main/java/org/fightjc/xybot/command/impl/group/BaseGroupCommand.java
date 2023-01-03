package org.fightjc.xybot.command.impl.group;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import org.fightjc.xybot.command.GroupCommand;
import org.fightjc.xybot.model.dto.ResultOutput;

import java.util.ArrayList;

public abstract class BaseGroupCommand implements GroupCommand {

    @Override
    public Message execute(Member sender, ArrayList<String> args, MessageChain messageChain, Group subject) throws Exception {
        ResultOutput result = checkRole(sender, subject);
        if (!result.getSuccess()) {
            return new At(sender.getId()).plus(result.getInfo());
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
    protected abstract Message executeHandle(Member sender, ArrayList<String> args, MessageChain messageChain, Group subject) throws Exception;

    /**
     * 权限判断
     * @return
     */
    protected abstract ResultOutput<String> checkRole(Member sender, Group subject);

    /**
     * 判断是否是群主
     * @param member
     * @return
     */
    protected boolean isGroupOwner(Member member) {
        MemberPermission permission = member.getPermission();
        return permission == MemberPermission.OWNER;
    }

    /**
     * 判断是否是群管理员
     * @param member
     * @return
     */
    protected boolean isGroupAdmin(Member member) {
        MemberPermission permission = member.getPermission();
        return permission == MemberPermission.ADMINISTRATOR;
    }
}
