package org.fightjc.xybot.command.impl;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;
import org.fightjc.xybot.annotate.CommandAnnotate;
import org.fightjc.xybot.annotate.SwitchAnnotate;
import org.fightjc.xybot.command.impl.group.MemberGroupCommand;
import org.fightjc.xybot.pojo.Command;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@CommandAnnotate
@SwitchAnnotate(name = "寒暄")
public class GreetingCommand extends MemberGroupCommand {

    @Override
    public Command property() {
        return new Command("早", "早安", "早上好", "晚", "晚安", "晚上好");
    }

    @Override
    protected Message executeHandle(Member sender, ArrayList<String> args, MessageChain messageChain, Group subject) throws Exception {
        At at = new At(sender.getId());
        String greeting = "你在说啥？";

        Random random = new Random();
        int seed = random.nextInt(20) + 1;
        if (seed == 1) { // 5%概率
            greeting = "不听不听，王八念经";
        } else {
            String rawMessage = messageChain.contentToString();
            if (rawMessage.contains("早")) {
                List<String> response = new ArrayList<String>() {{
                    add("早");
                    add("早安");
                    add("早上好");
                    add("早上好！新的一天可要元气满满啊ଘ(੭ˊ꒳\u200Bˋ)੭");
                    add("( ｣ﾟДﾟ)｣＜早早早");
                    add("早啊，不要忘记吃早餐哦，不然会饿坏小肚几的_(:з」∠)_");
                }};
                greeting = response.get(random.nextInt(response.size()));
            } else if (rawMessage.contains("晚")) {
                List<String> response = new ArrayList<String>() {{
                    add("晚");
                    add("晚安");
                    add("晚上好");
                    add("晚上好啊，辛苦了一天，是要先洗澡，还是先吃饭？还是~(*/ω＼*)");
                    add("晚上好！今晚可别忘了早点休息_(:з」∠)_");
                    add("晚上好！今天的任务还没有完成,你还不能休息");
                }};
                greeting = response.get(random.nextInt(response.size()));
            }
        }

        return at.plus(new PlainText(greeting));
    }
}
