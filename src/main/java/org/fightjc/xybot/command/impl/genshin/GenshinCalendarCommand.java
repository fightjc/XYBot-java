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
import org.fightjc.xybot.enums.ResultCode;
import org.fightjc.xybot.model.Command;
import org.fightjc.xybot.model.dto.ResultOutput;
import org.fightjc.xybot.module.genshin.pojo.GroupCalendarBean;
import org.fightjc.xybot.module.genshin.service.GenshinService;
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
        String usage = "使用方式：原神日历 [开启推送/关闭推送/状态]";

        long groupId = subject.getId();
        At at = new At(sender.getId());

        if (args.size() > 0) {
            String c = args.get(0);
            if (c.equals("开启推送") || c.equals("关闭推送")  || c.equals("状态")) {
                if (!isGroupAdmin(sender) && !isGroupOwner(sender)) {
                    return at.plus(new PlainText("当前用户无权限操作，请联系管理员！"));
                }
            }

            switch (c) {
                case "开启推送":
                case "关闭推送":
                    genshinService.createOrUpdateGroupCalendar(groupId, c.contains("开启"), sender.getId());
                    String ret = "原神日历推送已" + (c.contains("开启") ? "开启" : "关闭");
                    return at.plus(new PlainText(ret));
                case "状态":
                    GroupCalendarBean groupCalendarBean = genshinService.getGroupCalendarByGroupId(groupId);
                    if (groupCalendarBean != null) {
                        String info = "原神日历推送已" + (groupCalendarBean.isActive() ? "开启" : "关闭");
                        return at.plus(new PlainText(info));
                    } else {
                        return at.plus(new PlainText("当前群尚未开启原神日历推送功能"));
                    }
                default:
                    return new PlainText(usage);
            }
        }

        ResultOutput<BufferedImage> result = genshinService.getCalendar();
        if (ResultCode.SUCCESS.getCode() == result.getCode()) {
            BufferedImage image = result.getObject();
            if (image == null) {
                return at.plus(new PlainText(result.getMsg()));
            } else {
                ExternalResource resource = ImageUtil.bufferedImage2ExternalResource(image);
                return at.plus(subject.uploadImage(resource));
            }
        } else {
            return at.plus(new PlainText(result.getMsg()));
        }
    }
}
