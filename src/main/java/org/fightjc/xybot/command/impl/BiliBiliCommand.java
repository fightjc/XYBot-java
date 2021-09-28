package org.fightjc.xybot.command.impl;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;
import org.fightjc.xybot.annotate.CommandAnnotate;
import org.fightjc.xybot.annotate.SwitchAnnotate;
import org.fightjc.xybot.command.impl.group.AdminGroupCommand;
import org.fightjc.xybot.pojo.Command;
import org.fightjc.xybot.pojo.ResultOutput;
import org.fightjc.xybot.pojo.bilibili.DynamicBean;
import org.fightjc.xybot.pojo.bilibili.SubscribeBean;
import org.fightjc.xybot.service.BiliBiliService;
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
        if (args.size() <= 0 || 2 < args.size()) {
            return new PlainText("使用方式：b站 [列表] [up mid] [订阅/退订 mid]");
        }

        At at = new At(sender.getId());

        String opt = args.get(0);
        switch (opt) {
            case "列表":
                List<DynamicBean> dynamicBeanList = biliBiliService.getGroupSubscribes(subject.getId());

                StringBuilder message = new StringBuilder();
                for (DynamicBean bean : dynamicBeanList) {
                    message.append(bean.getName()).append(" ").append(bean.getMid()).append("\n");
                }

                return new PlainText("当前已订阅：\n" + message.toString());
            case "up":
                String upMid = args.get(1);

                ResultOutput<DynamicBean> result = biliBiliService.getUpInfo(upMid);
                if (result.getSuccess()) {
                    String info = result.getObject().getName() + "\n直接访问个人空间 https://space.bilibili.com/" + upMid;
                    at.plus(new PlainText(info));
                } else {
                    at.plus(new PlainText(result.getInfo()));
                }
            case "订阅":
            case "退订":
                String mid = args.get(1);
                Long groupId = subject.getId();
                boolean isSubscribe = opt.equals("订阅");

                // 检查mid是否存在
                ResultOutput<DynamicBean> resultOutput = biliBiliService.getUpInfo(mid);
                if (!resultOutput.getSuccess()) {
                    return at.plus(new PlainText("找不到要操作的对象，请再次检查mid"));
                }
                DynamicBean dynamicBean = resultOutput.getObject();

                // 检查是否已订阅
                SubscribeBean subscribeBean = biliBiliService.getGroupSubscribe(groupId, mid);
                if (subscribeBean == null && !isSubscribe) {
                    return at.plus(new PlainText("当前还未关注过 " + dynamicBean.getName()));
                }

                // 更新up信息
                biliBiliService.createOrUpdateDynamic(mid, dynamicBean.getName(), opt.equals("订阅"));

                // 群订阅
                biliBiliService.createOrUpdateGroupSubscribe(groupId, mid, opt.equals("订阅"), sender.getId());

                return at.plus(new PlainText("成功" + opt + " " + mid));
            default:
                return new PlainText("使用方式：b站 [列表] [订阅/退订 mid]");
        }
    }
}
