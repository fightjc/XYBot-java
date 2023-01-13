package org.fightjc.xybot.module.bot.command.impl.genshin;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.utils.ExternalResource;
import org.fightjc.xybot.module.bot.annotate.CommandAnnotate;
import org.fightjc.xybot.module.bot.annotate.SwitchAnnotate;
import org.fightjc.xybot.module.bot.command.impl.group.MemberGroupCommand;
import org.fightjc.xybot.enums.ResultCode;
import org.fightjc.xybot.model.Command;
import org.fightjc.xybot.model.dto.ResultOutput;
import org.fightjc.xybot.module.genshin.service.GenshinService;
import org.fightjc.xybot.module.genshin.service.impl.GenshinServiceImpl;
import org.fightjc.xybot.util.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Calendar;

@CommandAnnotate
@SwitchAnnotate(name = "每日素材")
public class DailyMaterialCommand extends MemberGroupCommand {

    @Autowired
    protected GenshinService genshinService;

    @Override
    public Command property() {
        return new Command("每日素材");
    }

    @Override
    protected Message executeHandle(Member sender, ArrayList<String> args, MessageChain messageChain, Group subject) throws Exception {
        At at = new At(sender.getId());

        if (args.size() > 1) {
            return at.plus(new PlainText("使用方式：每日素材 [星期X/周X/礼拜X]"));
        }

        ResultOutput<BufferedImage> result;
        if (args.size() == 0) {
            // 获取当天素材
            int week = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
            result = genshinService.getDailyMaterial(GenshinServiceImpl.DAILY_MATERIAL_WEEK.getWeek(week));
        } else {
            String opt = args.get(0)
                    .replace("星期", "")
                    .replace("周", "")
                    .replace("礼拜", "");
            switch (opt) {
                case "一": case "1":
                    result = genshinService.getDailyMaterial(GenshinServiceImpl.DAILY_MATERIAL_WEEK.MON);
                    break;
                case "二": case "2":
                    result = genshinService.getDailyMaterial(GenshinServiceImpl.DAILY_MATERIAL_WEEK.TUE);
                    break;
                case "三": case "3":
                    result = genshinService.getDailyMaterial(GenshinServiceImpl.DAILY_MATERIAL_WEEK.WED);
                    break;
                case "四": case "4":
                    result = genshinService.getDailyMaterial(GenshinServiceImpl.DAILY_MATERIAL_WEEK.THU);
                    break;
                case "五": case "5":
                    result = genshinService.getDailyMaterial(GenshinServiceImpl.DAILY_MATERIAL_WEEK.FRI);
                    break;
                case "六": case "6":
                    result = genshinService.getDailyMaterial(GenshinServiceImpl.DAILY_MATERIAL_WEEK.SAT);
                    break;
                case "日": case "天": case "7":
                    result = genshinService.getDailyMaterial(GenshinServiceImpl.DAILY_MATERIAL_WEEK.SUN);
                    break;
                default:
                    return at.plus(new PlainText("使用方式：每日素材 [星期X/周X/礼拜X]"));
            }
        }

        if (ResultCode.SUCCESS.getCode() != result.getStatus()) {
            return at.plus(new PlainText(result.getMsg()));
        } else {
            BufferedImage image = result.getData();
            if (image == null) {
                return at.plus(new PlainText(result.getMsg()));
            } else {
                ExternalResource resource = ImageUtil.bufferedImage2ExternalResource(image);
                return at.plus(subject.uploadImage(resource));
            }
        }

    }
}
