package org.fightjc.xybot.module.bilibili.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.utils.ExternalResource;
import org.fightjc.xybot.bot.XYBot;
import org.fightjc.xybot.enums.ResultCode;
import org.fightjc.xybot.module.bilibili.dao.BiliBiliDao;
import org.fightjc.xybot.module.bilibili.pojo.*;
import org.fightjc.xybot.model.dto.HttpClientResult;
import org.fightjc.xybot.model.dto.ResultOutput;
import org.fightjc.xybot.module.bilibili.service.BiliBiliService;
import org.fightjc.xybot.util.HttpClientUtil;
import org.fightjc.xybot.util.ImageUtil;
import org.fightjc.xybot.util.MessageUtil;
import org.fightjc.xybot.module.bilibili.BilibiliDynamicDrawHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.sql.Timestamp;
import java.util.*;
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
                return new ResultOutput<>(ResultCode.SUCCESS, "查询成功", builder.toString());
            } else {
                return new ResultOutput<>(ResultCode.FAILED, content.getString("message"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultOutput<>(ResultCode.FAILED, "请求网络失败");
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
                return new ResultOutput<>(ResultCode.SUCCESS, "查询成功", dto);
            } else {
                return new ResultOutput<>(ResultCode.FAILED, content.getString("message"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultOutput<>(ResultCode.FAILED, "请求网络失败");
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
        int max_result_count = 3; // 控制一次最多只显示多少条动态
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
                for (int i = 0; i < cards.size() && i < max_result_count; i++) {
                    JSONObject card = cards.getJSONObject(i);
                    JSONObject desc = card.getJSONObject("desc");

                    String dynamicId = desc.getString("dynamic_id");
                    if (i == 0) {
                        // 记录最新一条动态Id
                        latestDynamicId = dynamicId;
                    }

                    if (dynamicId.equals(offset)) {
                        // 没有更新
                        break;
                    }

                    long timestamp = desc.getLong("timestamp") * 1000;
                    if (now - timestamp > 5 * 60 * 1000) {
                        // 大于5分钟的不推送
                        break;
                    }

                    // 获取Up主信息
                    JSONObject userProfile = desc.getJSONObject("user_profile");
                    JSONObject info = userProfile.getJSONObject("info");
                    String uid = info.getString("uid");
                    String uname = info.getString("uname");
                    BufferedImage faceImage = ImageUtil.getImageFromUri(info.getString("face"));

                    // 获取头像信息
                    JSONObject pendant = userProfile.getJSONObject("pendant");
                    BufferedImage pendantImage = ImageUtil.getImageFromUri(pendant.getString("image"));

                    String date = MessageUtil.getDateTime(new Timestamp(timestamp));

                    int type = desc.getIntValue("type");
                    if (type == 2) {
                        JSONObject cardInfo = JSONObject.parseObject(card.getString("card"));
                        JSONObject item = cardInfo.getJSONObject("item");
                        String description = item.getString("description");
                        JSONArray pictures = item.getJSONArray("pictures");
                        List<DynamicPictureBean> imageList = new ArrayList<>();
                        for (int j = 0; j < pictures.size(); j++) {
                            JSONObject picture = pictures.getJSONObject(j);
                            String src = picture.getString("img_src");
                            int width = picture.getIntValue("img_width");
                            int height = picture.getIntValue("img_height");
                            imageList.add(new DynamicPictureBean(width, height, ImageUtil.getImageFromUri(src)));
                        }

                        dynamicDtoList.add(new DynamicPictureDto(uid, uname, faceImage, pendantImage,
                                dynamicId, date, type,
                                description, imageList));
                    } else if (type == 8) {
                        JSONObject cardInfo = JSONObject.parseObject(card.getString("card"));
                        String description = cardInfo.getString("desc");
                        BufferedImage pic = ImageUtil.getImageFromUri(cardInfo.getString("pic"));
                        String shortLink = cardInfo.getString("short_link");
                        String title = cardInfo.getString("title");
                        JSONObject statInfo = cardInfo.getJSONObject("stat"); // 视频统计信息
                        int view = statInfo.getIntValue("view");
                        int like = statInfo.getIntValue("like");
                        int coin = statInfo.getIntValue("coin");
                        int favorite = statInfo.getIntValue("favorite");

                        dynamicDtoList.add(new DynamicVideoDto(uid, uname, faceImage, pendantImage,
                                dynamicId, date, type,
                                description, shortLink, pic, title, view, like, coin, favorite));
                    }
                }

                // 记录最新动态
                updateDynamicOffset(mid, latestDynamicId);

                return new ResultOutput<>(ResultCode.SUCCESS, "获取动态成功", dynamicDtoList);
            } else {
                return new ResultOutput<>(ResultCode.FAILED, content.getString("message"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultOutput<>(ResultCode.FAILED, "请求网络失败");
        }
    }

    //endregion

    /**
     * 更新所有up动态并推送
     */
    public void checkNeedGroupNotify() {
        Map<String, List<DynamicDto>> latestDynamics = new HashMap<>();
        // 对同一动态做缓存减少画图时间
        Map<String, String> dynamicMsgMap = new HashMap<>();
        Map<String, ExternalResource> dynamicImageMap = new HashMap<>();

        // 获取所有最新动态
        List<DynamicBean> dynamicList = biliBiliDao.getAllDynamics();
        for (DynamicBean dynamic : dynamicList) {
            // 没有群订阅不访问网络减少请求
            if (dynamic.getFollower() == 0) {
                continue;
            }
            ResultOutput<List<DynamicDto>> latest = getLatestDynamic(dynamic.getMid(), ""/*dynamic.getOffset()*/);
            if (ResultCode.SUCCESS.getCode() == latest.getCode()) {
                latestDynamics.put(dynamic.getMid(), latest.getObject());
            }
        }

        // 所有群推送记录
        List<SubscribeBean> groupSubscribes = biliBiliDao.getAllGroupSubscribes();
        groupSubscribes.removeIf(bean -> !bean.isActive()); // 移除不需要推送的记录
        // 对群分组
        Map<Long, List<String>> result = groupSubscribes.stream().collect(
                Collectors.groupingBy(
                        SubscribeBean::getGroupId, Collectors.mapping(SubscribeBean::getMid, Collectors.toList())
                )
        );
        // 推送到群
        for (Long groupId : result.keySet()) {
            List<String> mids = result.get(groupId);
            for (String mid : mids) {
                List<DynamicDto> dynamicDtos = latestDynamics.get(mid);
                if (dynamicDtos == null) continue;
                for (DynamicDto dto : dynamicDtos) {
                    Group group = XYBot.getBot().getGroup(groupId);
                    if (group != null) {
                        if (dynamicMsgMap.containsKey(dto.getDynamicId())) {
                            group.sendMessage(
                                    new PlainText(dynamicMsgMap.get(dto.getDynamicId()))
                                            .plus(group.uploadImage(dynamicImageMap.get(dto.getDynamicId()))));
                        } else {
                            // 说明
                            String msg = dto.getUname() + " (" + dto.getUid() + ") 于 " +
                                    dto.getDateString() + " 发布了" + getTypeName(dto.getType()) + "\n" +
                                    "详情点击: http://t.bilibili.com/" + dto.getDynamicId() + "\n\n";
                            dynamicMsgMap.put(dto.getDynamicId(), msg);

                            // 图片
                            try {
                                BufferedImage dynamicImage = BilibiliDynamicDrawHelper.generateDynamic(dto);
                                ExternalResource image = ImageUtil.bufferedImage2ExternalResource(dynamicImage);
                                dynamicImageMap.put(dto.getDynamicId(), image);

                                group.sendMessage(new PlainText(msg).plus(group.uploadImage(image)));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * bilibili动态类型名称
     * @param type
     * @return
     */
    private String getTypeName(int type) {
        // TODO: 专栏 转发 投稿 文字 图片 直播分享
        switch (type) {
            case 1:
                return "转发";
            case 2:
                return "动态";
            case 8:
                return "视频";
            case 64:
                return "文章";
            default:
                return "未知类型";
        }
    }
}
