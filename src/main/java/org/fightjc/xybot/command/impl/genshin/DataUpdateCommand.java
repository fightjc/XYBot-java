package org.fightjc.xybot.command.impl.genshin;

import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;
import org.fightjc.xybot.annotate.CommandAnnotate;
import org.fightjc.xybot.command.impl.friend.AdminFriendCommand;
import org.fightjc.xybot.pojo.Command;
import org.fightjc.xybot.pojo.ResultOutput;
import org.fightjc.xybot.service.GenshinService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

@CommandAnnotate
public class DataUpdateCommand extends AdminFriendCommand {

    @Autowired
    protected GenshinService genshinService;

    @Override
    public Command property() {
        return new Command("数据更新");
    }

    @Override
    protected Message executeHandle(Friend sender, ArrayList<String> args, MessageChain messageChain, Friend subject) throws Exception {
        StringBuilder result = new StringBuilder("数据更新操作详情：");
        String usage = "使用方式：数据更新 [检查]";

        if (args.size() > 1) {
            return new PlainText(usage);
        }

        if (args.size() == 0) {
            // 更新每日素材图片
            ResultOutput result_udm = genshinService.updateDailyMaterial();
            result.append("\n").append(result_udm.getInfo());
        } else {
            String opt = args.get(0);
            switch (opt) {
                case "检查":
                    ResultOutput cgr = genshinService.checkGenshinResource();
                    result.append("\n").append(cgr.getInfo());
                    break;
                default:
                    return new PlainText(usage);
            }
        }

        return new PlainText(result.toString());
    }
}
