package org.fightjc.xybot.command.impl;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.ExternalResource;
import org.fightjc.xybot.annotate.CommandAnnotate;
import org.fightjc.xybot.annotate.SwitchAnnotate;
import org.fightjc.xybot.command.impl.group.AdminGroupCommand;
import org.fightjc.xybot.pojo.Command;
import org.fightjc.xybot.pojo.ResultOutput;
import org.fightjc.xybot.pojo.bilibili.DynamicBean;
import org.fightjc.xybot.pojo.bilibili.SubscribeBean;
import org.fightjc.xybot.pojo.bilibili.UserInfoDto;
import org.fightjc.xybot.service.BiliBiliService;
import org.fightjc.xybot.util.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@CommandAnnotate
@SwitchAnnotate(name = "b站订阅")
public class BiliBiliCommand extends AdminGroupCommand {

    @Autowired
    protected BiliBiliService biliBiliService;

    @Override
    public Command property() {
        return new Command("b站");
    }

    @Override
    protected Message executeHandle(Member sender, ArrayList<String> args, MessageChain messageChain, Group subject) throws Exception {
        String usage = "使用方式：b站 [列表] [查询 关键字] [up mid] [订阅/退订 mid]";

        if (args.size() <= 0 || 2 < args.size()) {
            return new PlainText(usage);
        }

        At at = new At(sender.getId());

        String opt = args.get(0);
        switch (opt) {
            case "列表":
                List<DynamicBean> dynamicBeanList = biliBiliService.getGroupSubscribes(subject.getId());

                int i = 0;
                StringBuilder message = new StringBuilder();
                for (DynamicBean bean : dynamicBeanList) {
                    message.append(bean.getName()).append(" ").append(bean.getMid()).append("\n");
                }

                return new PlainText("当前已订阅：" + dynamicBeanList.size() + " 个\n" + message.toString());
            case "test":
                biliBiliService.checkNeedGroupNotify();
                return null;
            case "查询":
                String keyword = args.get(1);
                ResultOutput<String> searchUserResult = biliBiliService.searchUser(keyword);
                if (searchUserResult.getSuccess()) {
                    String info = "为您查询到下面结果：\n" + searchUserResult.getObject();
                    return at.plus(new PlainText(info));
                } else {
                    return at.plus(new PlainText(searchUserResult.getInfo()));
                }
            case "up":
                String upMid = args.get(1);

                ResultOutput<UserInfoDto> getUpInfoResult = biliBiliService.getUpInfo(upMid);
                if (getUpInfoResult.getSuccess()) {
                    UserInfoDto userInfoDto = getUpInfoResult.getObject();
                    String info = userInfoDto.getName() + " (" + userInfoDto.getMid() + ")\n" +
                            "性别：" + userInfoDto.getSex() + "\n" +
                            "个性签名：" + userInfoDto.getSign() + "\n\n" +
                            "直接访问个人空间 https://space.bilibili.com/" + upMid;
                    ExternalResource faceImage = ImageUtil.getImageFromUri(userInfoDto.getFace());
                    return at.plus(subject.uploadImage(faceImage)).plus(new PlainText(info));
                } else {
                    return at.plus(new PlainText(getUpInfoResult.getInfo()));
                }
            case "订阅":
            case "退订":
                String mid = args.get(1);
                Long groupId = subject.getId();
                boolean isSubscribe = opt.equals("订阅");

                // 检查mid是否存在
                ResultOutput<UserInfoDto> resultOutput = biliBiliService.getUpInfo(mid);
                if (!resultOutput.getSuccess()) {
                    return at.plus(new PlainText("找不到要操作的对象，请再次检查mid"));
                }
                UserInfoDto userInfoDto = resultOutput.getObject();

                // 检查是否已订阅
                SubscribeBean subscribeBean = biliBiliService.getGroupSubscribe(groupId, mid);
                if ((subscribeBean == null || !subscribeBean.isActive())  && !isSubscribe) {
                    String msg = "当前还未订阅 " + userInfoDto.getName() + " (" + userInfoDto.getMid() + ")";
                    return at.plus(new PlainText(msg));
                }
                if (subscribeBean != null && subscribeBean.isActive() && isSubscribe) {
                    String msg = "当前已订阅过 " + userInfoDto.getName() + " (" + userInfoDto.getMid() + ")";
                    return at.plus(new PlainText(msg));
                }

                // 更新up信息
                biliBiliService.createOrUpdateDynamic(mid, userInfoDto.getName(), opt.equals("订阅"), "");

                // 群订阅
                biliBiliService.createOrUpdateGroupSubscribe(groupId, mid, opt.equals("订阅"), sender.getId());

                return at.plus(new PlainText("已成功" + opt + " " + userInfoDto.getName() + " (" + mid + "0"));
            default:
                return new PlainText(usage);
        }
    }
}
