package org.fightjc.xybot.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.utils.ExternalResource;
import org.fightjc.xybot.bot.XYBot;
import org.fightjc.xybot.dao.BiliBiliDao;
import org.fightjc.xybot.po.HttpClientResult;
import org.fightjc.xybot.pojo.ResultOutput;
import org.fightjc.xybot.pojo.bilibili.*;
import org.fightjc.xybot.service.BiliBiliService;
import org.fightjc.xybot.util.HttpClientUtil;
import org.fightjc.xybot.util.ImageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class BiliBiliServiceImpl implements BiliBiliService {

    private static final Logger logger = LoggerFactory.getLogger(BiliBiliServiceImpl.class);

    @Autowired
    public BiliBiliDao biliBiliDao;

    //region 数据库操作

    public List<DynamicBean> getGroupSubscribes(Long groupId) {
        List<DynamicBean> dynamicBeanList = biliBiliDao.getGroupSubscribeDetails(groupId);
        // 防止队列中有空结果
        dynamicBeanList.removeIf(Objects::isNull);

        return dynamicBeanList;
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

    public void createOrUpdateDynamic(String mid, String name, boolean isFollow, String offset) {
        DynamicBean dynamicBean = biliBiliDao.getDynamic(mid);
        if (dynamicBean == null) {
            createDynamic(mid, name, isFollow, offset);
        } else {
            updateDynamic(mid, name, isFollow, offset);
        }
    }

    private void createDynamic(String mid, String name, boolean isFollow, String offset) {
        // 新建记录，默认是关注
        DynamicBean dynamicBean = new DynamicBean(mid, name, 1L, offset);
        biliBiliDao.createDynamic(dynamicBean);
    }

    private void updateDynamic(String mid, String name, boolean isFollow, String offset) {
        DynamicBean dynamicBean = biliBiliDao.getDynamic(mid);
        dynamicBean.changeSubscribe(isFollow);
        biliBiliDao.updateDynamic(dynamicBean);
    }

    public void updateDynamicOffset(String mid, String offset) {
        DynamicBean dynamicBean = biliBiliDao.getDynamic(mid);
        dynamicBean.refreshOffset(offset);
        biliBiliDao.updateDynamic(dynamicBean);
    }

    //endregion

    //region 网络操作

    /**
     * 用关键字搜索b站用户
     * @param keyword
     * @return
     */
    public ResultOutput<String> searchUser(String keyword) {
        String url = "https://api.bilibili.com/x/web-interface/search/type";

        Map<String, String> params = new HashMap<String, String>() {{
            put("jsonp", "jsonp");
            put("search_type", "bili_user");
            put("keyword", keyword);
            //put("page", 1);
        }};

        HttpClientResult httpClientResult;
        try {
            httpClientResult = HttpClientUtil.doGet(url, null, params);
            JSONObject content = JSONObject.parseObject(httpClientResult.content);
            int code = content.getIntValue("code");
            if (code == 0) {
                JSONObject data = content.getJSONObject("data");
                JSONArray result = data.getJSONArray("result");
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < result.size() && i < 10; i++) { // 控制一次最多只显示10条数据
                    if (i > 0) {
                        builder.append("\n\n");
                    }
                    JSONObject userInfo = result.getJSONObject(i);
                    String mid = userInfo.getString("mid");
                    String uname = userInfo.getString("uname");
                    String fans = userInfo.getString("fans");
                    String usign = userInfo.getString("usign").trim();
                    builder.append(uname).append(" (").append(mid)
                            .append(")\n粉丝：").append(fans)
                            .append("\n个性签名：").append(usign);
                }
                return new ResultOutput<>(true, "查询成功", builder.toString());
            } else {
                return new ResultOutput<>(false, content.getString("message"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultOutput<>(false, "请求网络失败");
        }
    }

    /**
     * 获取b站up主信息
     * @param key
     */
    public ResultOutput<UserInfoDto> getUpInfo(String key) {
        String url = "https://api.bilibili.com/x/space/acc/info";

        Map<String, String> params = new HashMap<String, String>() {{
            put("mid", key);
        }};

        HttpClientResult httpClientResult;
        try {
            httpClientResult = HttpClientUtil.doGet(url, null, params);
            JSONObject content = JSONObject.parseObject(httpClientResult.content);
            int code = content.getIntValue("code");
            if (code == 0) {
                JSONObject data = content.getJSONObject("data");
                String mid = data.getString("mid");
                String name = data.getString("name");
                String sex = data.getString("sex");
                String sign = data.getString("sign");
                String face = data.getString("face");
                UserInfoDto dto = new UserInfoDto(mid, name, sex, face, sign);
                return new ResultOutput<>(true, "查询成功", dto);
            } else {
                return new ResultOutput<>(false, content.getString("message"));
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
    public ResultOutput<List<DynamicDto>> getLatestDynamic(String mid, String offset) {
        String url = "https://api.vc.bilibili.com/dynamic_svr/v1/dynamic_svr/space_history";

        Map<String, String> params = new HashMap<String, String>() {{
            put("host_uid", mid);
//            put("offset_dynamic_id", offset);
            put("need_top", "0");
            put("platform", "web");
        }};

        List<DynamicDto> dynamicDtoList = new ArrayList<>();
        HttpClientResult httpClientResult;
        try {
            httpClientResult = HttpClientUtil.doGet(url, null, params);
            JSONObject content = JSONObject.parseObject(httpClientResult.content);
            int code = content.getIntValue("code");
            if (code == 0) {
                JSONObject data = content.getJSONObject("data");
                JSONArray cards = data.getJSONArray("cards");

                String latestDynamicId = "";
                long now = new Date().getTime();
                for (int i = 0; i < cards.size() && i < 3; i++) { // 控制一次最多只显示3条动态
                    JSONObject card = cards.getJSONObject(i);
                    JSONObject desc = card.getJSONObject("desc");
                    String dynamicId = desc.getString("dynamic_id");

                    if (i == 0) {
                        // 记录最新一条动态Id
                        latestDynamicId = dynamicId;
                    }

                    long timestamp = desc.getLong("timestamp") * 1000;
                    if (now - timestamp > 5 * 60 * 1000) {
                        // 大于5分钟的不推送
                        break;
                    }

                    if (dynamicId.equals(offset)) {
                        // 没有更新
                        break;
                    }

                    int type = desc.getIntValue("type");
                    // 1: 转发 2: 新闻 8: 视频
                    if (type == 2) {
                        JSONObject cardInfo = JSONObject.parseObject(card.getString("card"));
                        JSONObject item = cardInfo.getJSONObject("item");
                        String description = item.getString("description");
                        JSONArray pictures = item.getJSONArray("pictures");
                        List<ExternalResource> imageList = new ArrayList<>();
                        for (int j = 0; j < pictures.size(); j++) {
                            JSONObject picture = pictures.getJSONObject(j);
                            String src = picture.getString("img_src");
                            imageList.add(ImageUtil.getImageFromUri(src));
                        }
                        dynamicDtoList.add(new DynamicDto(dynamicId, type, description, imageList));
                    } else if (type == 8) {
                        JSONObject cardInfo = JSONObject.parseObject(card.getString("card"));
                        String description = cardInfo.getString("desc");
                        List<ExternalResource> imageList = new ArrayList<>();
                        imageList.add(ImageUtil.getImageFromUri(cardInfo.getString("pic")));
                        dynamicDtoList.add(new DynamicDto(dynamicId, type, description, imageList));
                    }
                }

                // 记录最新动态
                updateDynamicOffset(mid, latestDynamicId);

                return new ResultOutput<>(true, "获取动态成功", dynamicDtoList);
            } else {
                return new ResultOutput<>(false, content.getString("message"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultOutput<>(false, "请求网络失败");
        }
    }

    //endregion

    /**
     * 更新所有up动态并推送
     */
    public void checkNeedGroupNotify() {
        Map<String, List<DynamicDto>> latestDynamics = new HashMap<>();

        // 获取所有最新动态
        List<DynamicBean> dynamicList = biliBiliDao.getAllDynamics();
        for (DynamicBean dynamic : dynamicList) {
            // 没有群订阅不访问网络减少请求
            if (dynamic.getFollower() == 0) {
                continue;
            }
            ResultOutput<List<DynamicDto>> latest = getLatestDynamic(dynamic.getMid(), ""/*dynamic.getOffset()*/);
            if (latest.getSuccess()) {
                latestDynamics.put(dynamic.getMid(), latest.getObject());
            }
        }

        // 群推送
        List<SubscribeBean> groupSubscribes = biliBiliDao.getAllGroupSubscribes();
        // 对群分组
        Map<Long, List<String>> result = groupSubscribes.stream().collect(
                Collectors.groupingBy(
                        SubscribeBean::getGroupId, Collectors.mapping(SubscribeBean::getMid, Collectors.toList())
                )
        );
        for (Long groupId : result.keySet()) {
            List<String> mids = result.get(groupId);
            for (String mid : mids) {
                List<DynamicDto> dynamicDtos = latestDynamics.get(mid);
                for (DynamicDto dto : dynamicDtos) {
                    Group group = XYBot.getBot().getGroup(groupId);
                    group.sendMessage(
                            new PlainText(dto.getDescription())
                                    .plus(group.uploadImage(dto.getImageList().get(0))));
                }
            }
        }
    }
}
