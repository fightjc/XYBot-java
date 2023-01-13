package org.fightjc.xybot.module.bot;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.ListenerHost;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.utils.BotConfiguration;
import net.mamoe.mirai.utils.ExternalResource;
import org.fightjc.xybot.util.XYBotLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class XYBot {

    private static final Logger logger = LoggerFactory.getLogger(XYBot.class);

    private static Bot miraiBot;

    public static Bot getBot() {
        return miraiBot;
    }

    public static void startBot(Long account, String password, String deviceInfo, List<ListenerHost> events, String netLog) {
        BotConfiguration config = new BotConfiguration();
        // 使用device.json存储设备信息
        config.fileBasedDeviceInfo(deviceInfo);
        // 切换协议
        config.setProtocol(BotConfiguration.MiraiProtocol.ANDROID_WATCH);
        // 使用自定义的logger
        config.setBotLoggerSupplier(bot -> new XYBotLogger());
        // 将net层输出写入文件
        config.redirectNetworkLogToDirectory(new File(netLog));

        miraiBot = BotFactory.INSTANCE.newBot(account, password, config);
        miraiBot.login();

        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2,SSLv3");//设置https协议，解决SSL peer shut down incorrectly的异常

        // 注册事件
        for (ListenerHost event : events) {
            // 给当前登录的bot注册事件
            miraiBot.getEventChannel().registerListenerHost(event);
//            GlobalEventChannel.INSTANCE.registerListenerHost(event);
        }
    }

    public static void sendGroupMessage(Long groupId, Message prevMsg, List<ExternalResource> imageList, Message postMsg) {
        Group group = miraiBot.getGroup(groupId);
        if (group != null) {
            for (ExternalResource image : imageList) {
                prevMsg.plus(group.uploadImage(image));
            }
            group.sendMessage(prevMsg.plus(postMsg));
        } else {
            logger.info("发送群消息失败：bot未加入群 " + groupId);
        }
    }
}
