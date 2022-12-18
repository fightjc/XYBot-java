package org.fightjc.xybot.module.bilibili.service;

import org.fightjc.xybot.pojo.ResultOutput;
import org.fightjc.xybot.module.bilibili.pojo.DynamicBean;
import org.fightjc.xybot.module.bilibili.pojo.DynamicDto;
import org.fightjc.xybot.module.bilibili.pojo.SubscribeBean;
import org.fightjc.xybot.module.bilibili.pojo.UserInfoDto;

import java.util.List;

public interface BiliBiliService {

    /**
     * 用关键字搜索b站用户
     * @param keyword
     * @return
     */
    ResultOutput<String> searchUser(String keyword);

    /**
     * 获取b站up主信息
     * @param mid
     */
    ResultOutput<UserInfoDto> getUpInfo(String mid);

    /**
     * 获取指定群b站订阅列表
     * @param groupId
     * @return
     */
    List<DynamicBean> getGroupSubscribes(Long groupId);

    /**
     * 查询指定群是否已关注某up
     * @param groupId
     * @param mid
     * @return
     */
    SubscribeBean getGroupSubscribe(Long groupId, String mid);

    /**
     * 新增或修改up信息
     * @param mid
     * @param isFollow
     */
    void createOrUpdateDynamic(String mid, String name, boolean isFollow, String offset);

    /**
     * 新增或修改一条群订阅
     * @param groupId
     * @param mid
     * @param subscribe
     * @param modifiedUserId
     */
    void createOrUpdateGroupSubscribe(Long groupId, String mid, boolean subscribe, Long modifiedUserId);

    /**
     * 检查是否需要推送订阅更新
     */
    void checkNeedGroupNotify();

    /**
     * 获取up主最新动态
     * @param mid
     * @param offset
     * @return
     */
    ResultOutput<List<DynamicDto>> getLatestDynamic(String mid, String offset);
}
