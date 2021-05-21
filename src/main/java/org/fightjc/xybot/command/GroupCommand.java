package org.fightjc.xybot.command;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.ArrayList;

public interface GroupCommand extends BaseCommand {

    /**
     * 群聊指令解析和执行
     * @param sender 发送人
     * @param args 参数
     * @param messageChain 第一个元素一定为 [MessageSource]，存储此消息的发送人，发送时间，收信人，消息id等数据
     * @param subject 消息事件主体
     * @return 需要回复的内容，返回null不做处理
     * @throws Exception
     */
    Message execute(Member sender, ArrayList<String> args, MessageChain messageChain, Group subject) throws Exception;
}
