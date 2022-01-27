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
import org.fightjc.xybot.util.BotUtil;
import org.fightjc.xybot.util.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.image.BufferedImage;
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
            return at.plus(new PlainText("使用方式：查询 [钓鱼] [砍树] [角色/武器/物品]"));
        }

        // 查询物品
        String item = args.get(0);

        switch (item) {
            case "钓鱼":
                String fishingPath1 = BotUtil.getGenshinFolderPath() + "/images/miscellaneous/fishing_1.png";
                BufferedImage fishingImage1 = BotUtil.readImageFile(fishingPath1);

                String fishingPath2 = BotUtil.getGenshinFolderPath() + "/images/miscellaneous/fishing_2.jpg";
                BufferedImage fishingImage2 = BotUtil.readImageFile(fishingPath2);

                if (fishingImage1 != null && fishingImage2 != null) {
                    ExternalResource resource1 = ImageUtil.bufferedImage2ExternalResource(fishingImage1);
                    ExternalResource resource2 = ImageUtil.bufferedImage2ExternalResource(fishingImage2);
                    return at.plus(subject.uploadImage(resource1)).plus(subject.uploadImage(resource2));
                } else {
                    return at.plus(new PlainText("图片素材丢失，请联系管理员！"));
                }
            case "砍树":
                String creationPath = BotUtil.getGenshinFolderPath() + "/images/miscellaneous/creation.jpg";
                BufferedImage creationImage = BotUtil.readImageFile(creationPath);

                if (creationImage != null) {
                    ExternalResource resource = ImageUtil.bufferedImage2ExternalResource(creationImage);
                    return at.plus(subject.uploadImage(resource));
                } else {
                    return at.plus(new PlainText("图片素材丢失，请联系管理员！"));
                }
            default:
                ResultOutput<BufferedImage> result = genshinService.getInfoByName(item);

                if (!result.getSuccess()) {
                    return at.plus(new PlainText(result.getInfo()));
                } else {
                    BufferedImage image = result.getObject();
                    if (image == null) {
                        return at.plus(new PlainText(result.getInfo()));
                    } else {
                        ExternalResource resource = ImageUtil.bufferedImage2ExternalResource(image);
                        return at.plus(subject.uploadImage(resource));
                    }
                }
        }

    }
}
