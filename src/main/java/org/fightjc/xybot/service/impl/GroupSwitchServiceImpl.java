package org.fightjc.xybot.service.impl;

import org.fightjc.xybot.dao.GroupSwitchDao;
import org.fightjc.xybot.model.entity.GroupSwitch;
import org.fightjc.xybot.model.entity.GroupSwitchRecord;
import org.fightjc.xybot.service.GroupSwitchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupSwitchServiceImpl implements GroupSwitchService {

    private static final Logger logger = LoggerFactory.getLogger(GroupSwitchServiceImpl.class);

    @Autowired
    public GroupSwitchDao groupSwitchDao;

    public List<GroupSwitch> getGroupSwitchesByGroupId(Long groupId) {
        return groupSwitchDao.getAllGroupSwitches(groupId);
    }

    public GroupSwitch getGroupSwitch(Long groupId, String name) {
        return groupSwitchDao.getGroupSwitch(groupId, name);
    }

    public void createOrUpdateGroupSwitch(Long groupId, String name, boolean isOn, Long modifiedUserId) {
        GroupSwitch groupSwitch = groupSwitchDao.getGroupSwitch(groupId, name);
        if (groupSwitch == null) {
            createGroupSwitch(groupId, name, isOn, modifiedUserId);
        } else {
            updateGroupSwitch(groupId, name, isOn, modifiedUserId);
        }
    }

    private void createGroupSwitch(Long groupId, String name, boolean isOn, Long modifiedUserId) {
        GroupSwitch groupSwitch = new GroupSwitch(groupId, name, isOn);
        groupSwitchDao.createGroupSwitch(groupSwitch);
        GroupSwitchRecord groupSwitchRecord = new GroupSwitchRecord(groupSwitch, modifiedUserId);
        groupSwitchDao.createGroupSwitchRecord(groupSwitchRecord);
    }

    private void updateGroupSwitch(Long groupId, String name, boolean isOn, Long modifiedUserId) {
        GroupSwitch groupSwitch = new GroupSwitch(groupId, name, isOn);
        groupSwitchDao.updateGroupSwitch(groupSwitch);
        GroupSwitchRecord groupSwitchRecord = new GroupSwitchRecord(groupSwitch, modifiedUserId);
        groupSwitchDao.createGroupSwitchRecord(groupSwitchRecord);
    }
}
