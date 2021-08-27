package org.fightjc.xybot.command.impl.genshin;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.*;
import org.fightjc.xybot.annotate.CommandAnnotate;
import org.fightjc.xybot.annotate.SwitchAnnotate;
import org.fightjc.xybot.command.impl.group.MemberGroupCommand;
import org.fightjc.xybot.pojo.Command;
import org.fightjc.xybot.pojo.ResultOutput;
import org.fightjc.xybot.service.GenshinService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

@CommandAnnotate
@SwitchAnnotate(name = "原神查询")
public class GenshinSearchCommand extends MemberGroupCommand {

    @Autowired
    protected GenshinService genshinService;

    @Override
    public Command property() {
        return new Command("查询");
    }

    @Override
    protected Message executeHandle(Member sender, ArrayList<String> args, MessageChain messageChain, Group subject) throws Exception {
        At at = new At(sender.getId());

        // 检查命令格式
        if (args.size() != 1) {
            return at.plus(new PlainText("使用方式：查询 [角色]"));
        }

        // 查询物品
        String item = args.get(0);
        ResultOutput<String> result = genshinService.getInfoByName(item);
        if (result.getSuccess()) {
            return at.plus(new LightApp(result.getObject()));
        } else {
            return at.plus(new PlainText(result.getInfo()));
        }
    }
}
