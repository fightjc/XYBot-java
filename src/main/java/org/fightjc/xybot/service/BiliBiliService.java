package org.fightjc.xybot.service;

import org.fightjc.xybot.pojo.bilibili.DynamicBean;

import java.util.List;

public interface BiliBiliService {

    /**
     * 获取指定群b站订阅列表
     * @param groupId
     * @return
     */
    List<DynamicBean> getGroupSubscribes(Long groupId);

    /**
     * 新增或修改一条群订阅
     * @param groupId
     * @param mid
     * @param subscribe
     * @param modifiedUserId
     */
    void createOrUpdateGroupSubscribe(Long groupId, String mid, boolean subscribe, Long modifiedUserId);
}
