package org.fightjc.xybot.command.impl.genshin;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.utils.ExternalResource;
import org.fightjc.xybot.annotate.CommandAnnotate;
import org.fightjc.xybot.annotate.SwitchAnnotate;
import org.fightjc.xybot.command.impl.group.MemberGroupCommand;
import org.fightjc.xybot.pojo.Command;
import org.fightjc.xybot.pojo.ResultOutput;
import org.fightjc.xybot.service.GenshinService;
import org.fightjc.xybot.util.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

@CommandAnnotate
@SwitchAnnotate(name = "原神日历")
public class GenshinCalendarCommand extends MemberGroupCommand {

    @Autowired
    protected GenshinService genshinService;

    @Override
    public Command property() {
        return new Command("原神日历");
    }

    @Override
    protected Message executeHandle(Member sender, ArrayList<String> args, MessageChain messageChain, Group subject) throws Exception {
        String usage = "使用方式：原神日历";

//        if (args.size() > 0) {
//            return new PlainText(usage);
//        }

        At at = new At(sender.getId());

        ResultOutput<BufferedImage> result = genshinService.getCalendar();
        if (result.getSuccess()) {
            BufferedImage image = result.getObject();
            if (image == null) {
                return at.plus(new PlainText(result.getInfo()));
            } else {
                ExternalResource resource = ImageUtil.bufferedImage2ExternalResource(image);
                return at.plus(subject.uploadImage(resource));
            }
        } else {
            return at.plus(new PlainText(result.getInfo()));
        }
    }
}
