package org.fightjc.xybot.dao;

import org.apache.ibatis.annotations.Param;
import org.fightjc.xybot.pojo.GroupSwitch;
import org.fightjc.xybot.pojo.GroupSwitchRecord;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupSwitchDao {

    /**
     * 获取所有群功能开关状态
     * @return
     */
    List<GroupSwitch> getAllGroupSwitches(Long groupId);

    /**
     * 获取群功能开关状态
     * @param groupId
     * @param name
     * @return
     */
    GroupSwitch getGroupSwitch(@Param("groupId") Long groupId, @Param("name") String name);

    /**
     * 插入一条群功能开关状态
     * @param groupSwitch
     */
    void createGroupSwitch(GroupSwitch groupSwitch);

    /**
     * 更新群功能开关状态
     * @param groupSwitch
     */
    void updateGroupSwitch(GroupSwitch groupSwitch);

    /**
     * 插入一条改变群功能开关记录
     * @param record
     */
    void createGroupSwitchRecord(GroupSwitchRecord record);
}
