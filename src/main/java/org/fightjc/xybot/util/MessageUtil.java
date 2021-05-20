package org.fightjc.xybot.util;

import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.SingleMessage;

public class MessageUtil {

    /**
     * 只获取文字信息
     * @param messages
     * @return
     */
    public static String filterMessage(MessageChain messages) {
        String temp = "";
        for (int i = 0; i < messages.size(); i++) {
            String msg = "" + messages.get(i);
            if (msg.indexOf("[mirai:") == -1) {
                temp += msg.replace("\r", " ").trim();
            }
        }
        return temp;
    }
}
