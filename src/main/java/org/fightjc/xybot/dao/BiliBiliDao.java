package org.fightjc.xybot.dao;

import org.apache.ibatis.annotations.Param;
import org.fightjc.xybot.pojo.bilibili.DynamicBean;
import org.fightjc.xybot.pojo.bilibili.SubscribeBean;
import org.fightjc.xybot.pojo.bilibili.SubscribeRecordBean;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BiliBiliDao {

    /**
     * 获取指定群的所有订阅记录
     * @param groupId
     * @return
     */
    List<DynamicBean> getGroupSubscribeDetails(Long groupId);

    /**
     * 获取所有群订阅列表
     * @return
     */
    List<SubscribeBean> getAllGroupSubscribes();

    /**
     * 获取指定群指定的订阅记录
     * @param groupId
     * @param mid
     * @return
     */
    SubscribeBean getGroupSubscribe(@Param("groupId") Long groupId, @Param("mid") String mid);

    /**
     * 插入一条群订阅
     * @param subscribeBean
     */
    void createGroupSubscribe(SubscribeBean subscribeBean);

    /**
     * 更新一条群订阅
     * @param subscribeBean
     */
    void updateGroupSubscribe(SubscribeBean subscribeBean);

    /**
     * 插入一条修改群订阅记录
     * @param subscribeRecordBean
     */
    void createGroupSubscribeRecord(SubscribeRecordBean subscribeRecordBean);

    List<DynamicBean> getAllDynamics();

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
