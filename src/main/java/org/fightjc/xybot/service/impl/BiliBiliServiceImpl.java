package org.fightjc.xybot.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.fightjc.xybot.bot.XYBot;
import org.fightjc.xybot.dao.BiliBiliDao;
import org.fightjc.xybot.po.HttpClientResult;
import org.fightjc.xybot.pojo.ResultOutput;
import org.fightjc.xybot.pojo.bilibili.DynamicBean;
import org.fightjc.xybot.pojo.bilibili.SubscribeBean;
import org.fightjc.xybot.pojo.bilibili.SubscribeRecordBean;
import org.fightjc.xybot.service.BiliBiliService;
import org.fightjc.xybot.util.HttpClientUtil;
import org.jetbrains.annotations.NotNull;
import org.jpedal.parser.shape.D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BiliBiliServiceImpl implements BiliBiliService {

    private static final Logger logger = LoggerFactory.getLogger(BiliBiliServiceImpl.class);

    @Autowired
    public BiliBiliDao biliBiliDao;

    //region 数据库操作

    public List<DynamicBean> getGroupSubscribes(Long groupId) {
        return biliBiliDao.getGroupSubscribeDetails(groupId);
    }

    public SubscribeBean getGroupSubscribe(Long groupId, String mid) {
        return biliBiliDao.getGroupSubscribe(groupId, mid);
    }

    public void createOrUpdateGroupSubscribe(Long groupId, String mid, boolean isActive, Long modifiedUserId) {
        // 群订阅记录
        SubscribeBean subscribeBean = biliBiliDao.getGroupSubscribe(groupId, mid);
        if (subscribeBean == null) {
            createGroupSubscribe(groupId, mid, isActive, modifiedUserId);
        } else {
            updateGroupSubscribe(groupId, mid, isActive, modifiedUserId);
        }
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

    public void createOrUpdateDynamic(String mid, String name, boolean isFollow) {
        DynamicBean dynamicBean = biliBiliDao.getDynamic(mid);
        if (dynamicBean == null) {
            createDynamic(mid, name, isFollow);
        } else {
            updateDynamic(mid, name, isFollow);
        }
    }

    private void createDynamic(String mid, String name, boolean isFollow) {
        // 新建记录，默认是关注
        DynamicBean dynamicBean = new DynamicBean(mid, name, 1L, "");
        biliBiliDao.createDynamic(dynamicBean);
    }

    private void updateDynamic(String mid, String name, boolean isFollow) {
        DynamicBean dynamicBean = biliBiliDao.getDynamic(mid);
        dynamicBean.changeSubscribe(isFollow);
        biliBiliDao.updateDynamic(dynamicBean);
    }

    //endregion

    //region 网络操作

    /**
     * 获取b站up主信息
     * @param mid
     */
    public ResultOutput<DynamicBean> getUpInfo(String mid) {
        String url = "https://api.bilibili.com/x/space/acc/info";

        Map<String, String> params = new HashMap<String, String>() {{
            put("mid", mid);
        }};

        HttpClientResult httpClientResult;
        try {
            httpClientResult = HttpClientUtil.doGet(url, null, params);
            JSONObject result = JSONObject.parseObject(httpClientResult.content);
            int code = result.getIntValue("code");
            if (code == 0) {
                JSONObject data = result.getJSONObject("data");
                String name = data.getString("name");
                return new ResultOutput<>(true, "查询成功", new DynamicBean(mid, name, 0L, null));
            } else {
                return new ResultOutput<>(false, result.getString("message"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultOutput<>(false, "请求网络失败");
        }
    }

    /**
     * 获取up主最新动态
     * @param mid
     * @param offset
     * @return
     */
    public List<String> getLatestDynamic(String mid, String offset) {
        String url = "https://api.vc.bilibili.com/dynamic_svr/v1/dynamic_svr/space_history";

        Map<String, String> params = new HashMap<String, String>() {{
            put("host_uid", mid);
            put("offset_dynamic_id", offset);
            put("need_top", "0");
            put("platform", "web");
        }};

        HttpClientResult result;
        try {
            result = HttpClientUtil.doGet(url, null, params);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<String>();
    }

    //endregion

    /**
     * 更新所有up动态并推送
     */
    public void checkNeedGroupNotify() {
        Map<String, List<String>> latestDynamics = new HashMap<>();

        // 获取所有最新动态
        List<DynamicBean> dynamicList = biliBiliDao.getAllDynamics();
        for (DynamicBean dynamic : dynamicList) {
            List<String> latest = getLatestDynamic(dynamic.getMid(), dynamic.getOffset());
            latestDynamics.put(dynamic.getMid(), latest);
        }

        // TODO: 群推送
        Map<Long, List<DynamicBean>> groupSubscribes = biliBiliDao.getAllGroupSubscribes();
        for (Long groupId : groupSubscribes.keySet()) {
            List<DynamicBean> dynamicBeans = groupSubscribes.get(groupId);
            for (DynamicBean dynamicBean : dynamicBeans) {
                List<String> messages = latestDynamics.get(dynamicBean.getMid());
                for (String message : messages) {
//                    XYBot.getBot().getGroup(groupId).sendMessage(message);
                }
            }
        }
    }
}
