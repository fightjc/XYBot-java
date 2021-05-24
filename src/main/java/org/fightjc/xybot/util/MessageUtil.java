package org.fightjc.xybot.util;

import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.SingleMessage;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageUtil {

    public static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

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

    /**
     * 获取当前时间戳 yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String getCurrentDateTime() {
        return dateTimeFormat.format(new Date());
    }

    /**
     * 获取当前时间戳 yyyy-MM-dd
     * @return
     */
    public static String getCurrentDate() {
        return dateFormat.format(new Date());
    }
}
