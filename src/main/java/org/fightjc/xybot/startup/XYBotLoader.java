package org.fightjc.xybot.startup;

import net.mamoe.mirai.event.ListenerHost;
import org.fightjc.xybot.annotate.AnnotateAnalyzer;
import org.fightjc.xybot.bot.XYBot;
import org.fightjc.xybot.db.DBInitHelper;
import org.fightjc.xybot.db.DBMigration;
import org.fightjc.xybot.events.CommandEvents;
import org.fightjc.xybot.pojo.GroupSwitch;
import org.fightjc.xybot.service.GroupSwitchService;
import org.fightjc.xybot.util.BotSwitch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class XYBotLoader implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(XYBotLoader.class);

    @Value("${bot.account}")
    Long account;

    @Value("${bot.password}")
    String password;

    @Value("${device.fileName}")
    String deviceInfo;

    @Value("${logging.net.path}")
    String logNetPath;

    @Autowired
    @Qualifier("allBotEvents")
    List<ListenerHost> events;

    @Autowired
    DBMigration dbMigration;

    @Autowired
    GroupSwitchService groupSwitchService;

    @Autowired
    AnnotateAnalyzer annotateAnalyzer;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 准备数据库
        prepareDatabase();
        // 启动bot
        startupBot();
    }

    private void prepareDatabase() {
        // 确保存在数据库文件
        DBInitHelper.getInstance().prepareDB();
        // 更新数据库
        dbMigration.updateDB();

        // 初始化GroupSwitch
        List<GroupSwitch> groupSwitchList = groupSwitchService.getGroupSwitchesByGroupId(null);
        for (GroupSwitch groupSwitch : groupSwitchList) {
            BotSwitch.getInstance().createOrUpdateGroupSwitch(groupSwitch.getGroupId(), groupSwitch.getName(), groupSwitch.isOn());
        }

    }

    private void startupBot() {
        // 添加自定义指令事件，前置指令事件，优先处理
        CommandEvents commandEvents = new CommandEvents();
        commandEvents.registerCommandHeaders("");
        commandEvents.registerCommands(annotateAnalyzer.getCommands());
        events.add(0, commandEvents);

        XYBot.startBot(account, password, deviceInfo, events, logNetPath);

        // 启动新线程跑bot不占用主线程
        new Thread(() -> XYBot.getBot().join()).start();

        logger.info("启动XYBot成功！");
    }
}
