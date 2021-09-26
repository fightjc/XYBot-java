package org.fightjc.xybot.service.impl;

import org.fightjc.xybot.dao.BiliBiliDao;
import org.fightjc.xybot.pojo.ResultOutput;
import org.fightjc.xybot.pojo.bilibili.DynamicBean;
import org.fightjc.xybot.pojo.bilibili.SubscribeBean;
import org.fightjc.xybot.pojo.bilibili.SubscribeRecordBean;
import org.fightjc.xybot.service.BiliBiliService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class BiliBiliServiceImpl implements BiliBiliService {

    private static final Logger logger = LoggerFactory.getLogger(BiliBiliServiceImpl.class);

    @Autowired
    public BiliBiliDao biliBiliDao;

    public List<DynamicBean> getGroupSubscribes(Long groupId) {
        return biliBiliDao.getGroupSubscribeDetails(groupId);
    }

    public void createOrUpdateGroupSubscribe(Long groupId, String mid, boolean isActive, Long modifiedUserId) {
        // 群订阅记录
        SubscribeBean subscribeBean = biliBiliDao.getGroupSubscribe(groupId, mid);
        if (subscribeBean == null) {
            createGroupSubscribe(groupId, mid, isActive, modifiedUserId);
        } else {
            updateGroupSubscribe(groupId, mid, isActive, modifiedUserId);
        }

        // 订阅信息
        createOrUpdateDynamic(mid, isActive);
    }

    private void createGroupSubscribe(Long groupId, String mid, boolean isActive, Long modifiedUserId) {
        SubscribeBean subscribeBean = new SubscribeBean(groupId, mid, isActive);
        biliBiliDao.createGroupSubscribe(subscribeBean);
        SubscribeRecordBean subscribeRecordBean = new SubscribeRecordBean(subscribeBean, modifiedUserId);
        biliBiliDao.createGroupSubscribeRecord(subscribeRecordBean);
    }

    private void updateGroupSubscribe(Long groupId, String mid, boolean isActive, Long modifiedUserId) {
        SubscribeBean subscribeBean = new SubscribeBean(groupId, mid, isActive);
        biliBiliDao.updateGroupSubscribe(subscribeBean);
        SubscribeRecordBean subscribeRecordBean = new SubscribeRecordBean(subscribeBean, modifiedUserId);
        biliBiliDao.createGroupSubscribeRecord(subscribeRecordBean);
    }

    private void createOrUpdateDynamic(String mid, boolean isFollow) {
        DynamicBean dynamicBean = biliBiliDao.getDynamic(mid);
        if (dynamicBean == null) {

        } else {

        }
    }

    /**
     * 访问b站获取订阅信息
     * @param mid
     */
    public void getInfo(String mid) {
        String url = "https://api.bilibili.com/x/space/acc/info?mid=" + mid;
    }

    public void getLatestDynamic(List<String> midList) {
    }

    public void getDynamic(String mid, String offset) {
        String url = "https://api.vc.bilibili.com/dynamic_svr/v1/dynamic_svr/space_history" +
                "?host_uid=" + mid + "&offset_dynamic_id=" + offset +"&need_top=0&platform=web";
    }
}
