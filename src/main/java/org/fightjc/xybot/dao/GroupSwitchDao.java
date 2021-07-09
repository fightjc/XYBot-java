package org.fightjc.xybot.dao;

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
    List<GroupSwitch> getAllGroupSwitches();

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
