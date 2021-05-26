package org.fightjc.xybot.command.impl.group;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;
import org.fightjc.xybot.annotate.CommandAnnotate;
import org.fightjc.xybot.pojo.Command;
import org.fightjc.xybot.pojo.Gacha;
import org.fightjc.xybot.util.BotGacha;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@CommandAnnotate
public class DailyLuckCommand extends MemberGroupCommand {

    BotGacha dailyGacha;

    public DailyLuckCommand() {
        Map<Gacha, Integer> items = new HashMap<>();
        items.put(new Gacha("", ""), 0);

        items.put(new Gacha("末吉", "谨记鬼魅多生自人心。"), 10);
        items.put(new Gacha("末吉", "是非难辨，凡事宜忍。"), 10);
        items.put(new Gacha("末吉", "人间有人泣，鬼界有鬼哭。"), 10);
        items.put(new Gacha("末吉", "是生灭法，多加小心。"), 10);
        items.put(new Gacha("末吉", "莫与独鬼相语，所经之途必遭祸。"), 10);
        items.put(new Gacha("末吉", "莫仿飞蛾事，徒然扑夜灯，阴阳自有道，相顺不相违。"), 10);
        items.put(new Gacha("末吉", "鬼火之处，身似孤魂，前路何往，知者无人。"), 10);

        items.put(new Gacha("半吉", "默念口中咒，所言非所思。"), 8);
        items.put(new Gacha("半吉", "吾身如浮萍，不敢言再会，幸得天眷顾，得挚友两三。"), 8);
        items.put(new Gacha("半吉", "应慎言，应慎行。"), 8);
        items.put(new Gacha("半吉", "天命如露滴，如幻更似虚，相逢若相知，逝去也足矣。"), 8);
        items.put(new Gacha("半吉", "胸中虽怀难言处，幸得有人问苦衷。"), 8);
        items.put(new Gacha("半吉", "沉浮尘世间，不必徒自添烦恼。"), 8);
        items.put(new Gacha("半吉", "诸事虽无常，自有潇洒意。"), 8);
        items.put(new Gacha("半吉", "鬼恶犹可治 人恶却难改。"), 8);

        items.put(new Gacha("小吉", "世人皆俱魑魅物，鬼妖之中亦有情。"), 7);
        items.put(new Gacha("小吉", "鬼无心，神有眼，五行有道。"), 7);
        items.put(new Gacha("小吉", "天上悬明月，清辉照万方，浮云随暂避，终不灭清光。"), 7);
        items.put(new Gacha("小吉", "身愿随心，对明月，有圆缺。"), 7);
        items.put(new Gacha("小吉", "春樱虽随风飘散，仍有余香风转廊。"), 7);
        items.put(new Gacha("小吉", "人间黄泉路，切记有归途。"), 7);
        items.put(new Gacha("小吉", "八重樱花繁且枝盛，君须记累瓣必偿情。"), 7);

        items.put(new Gacha("中吉", "满山枫似锦，祭神灵，运可转之。"), 5);
        items.put(new Gacha("中吉", "人间冥界均无事，难得一日可清闲。"), 5);
        items.put(new Gacha("中吉", "山中红叶犹可见，前程美景在暗中。"), 5);
        items.put(new Gacha("中吉", "人间渺渺栖百鬼，天地四处皆为家。"), 5);
        items.put(new Gacha("中吉", "邪灵恶鬼，终得严惩，不必担忧。"), 5);
        items.put(new Gacha("中吉", "是非难辨，凡事宜忍。"), 5);
        items.put(new Gacha("中吉", "诸行虽无常，自有潇洒意。"), 5);
        items.put(new Gacha("中吉", "从占卜之向，尊阴阳之道。"), 5);

        items.put(new Gacha("大吉", "闲适如春樱，清净如秋水。"), 3);
        items.put(new Gacha("大吉", "时光如轮转，时运今再来。"), 3);
        items.put(new Gacha("大吉", "人世清净，鬼界安宁，休矣。"), 3);
        items.put(new Gacha("大吉", "朦胧春月夜，美景世无双。"), 3);
        items.put(new Gacha("大吉", "四季交替，八方平安。"), 3);

        dailyGacha = new BotGacha(items);
    }

    @Override
    public Command property() {
        return new Command("抽签");
    }

    @Override
    protected Message executeHandle(Member sender, ArrayList<String> args, MessageChain messageChain, Group subject) throws Exception {
        String content = dailyGacha.getGacha(sender.getId());

        At at = new At(sender.getId());
        PlainText plainText = new PlainText(content);
        return at.plus(plainText);
    }
}
