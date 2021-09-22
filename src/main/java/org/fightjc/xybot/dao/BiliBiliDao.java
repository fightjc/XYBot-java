package org.fightjc.xybot.dao;

import org.fightjc.xybot.pojo.bilibili.DynamicBean;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BiliBiliDao {

    List<String> getGroupSubscribes(Long groupId);

    /**
     * 最近一次获取b站Up动态位置
     * @return
     */
    DynamicBean getDynamic();

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
