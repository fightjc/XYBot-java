package org.fightjc.xybot.dao;

import org.fightjc.xybot.pojo.bilibili.DynamicBean;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface BiliBiliDao {

    Map<Long, List<DynamicBean>> getAllGroupSubscribes();

    List<DynamicBean> getGroupSubscribes(Long groupId);

    /**
     * 最近一次获取b站Up动态位置
     * @return
     */
    DynamicBean getDynamic(String mid);

    /**
     * 插入一条获取b站Up动态位置的记录
     * @param dynamicBean
     */
    void createDynamic(DynamicBean dynamicBean);

    /**
     * 更新获取b站Up动态位置记录
     * @param dynamicBean
     */
    void updateDynamic(DynamicBean dynamicBean);
}
