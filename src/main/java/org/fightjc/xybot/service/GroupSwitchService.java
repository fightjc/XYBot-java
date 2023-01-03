package org.fightjc.xybot.service;

import org.fightjc.xybot.model.entity.GroupSwitch;

import java.util.List;

public interface GroupSwitchService {

    /**
     * 获取指定群所有开关
     * @param groupId
     * @return
     */
    List<GroupSwitch> getGroupSwitchesByGroupId(Long groupId);

    /**
     * 获取一条群开关
     * @param groupId
     * @param name
     * @return
     */
    GroupSwitch getGroupSwitch(Long groupId, String name);

    /**
     * 新增或修改一条群开关
     * @param groupId
     * @param name
     * @param isOn
     * @param modifiedUserId
     */
    void createOrUpdateGroupSwitch(Long groupId, String name, boolean isOn, Long modifiedUserId);
}
