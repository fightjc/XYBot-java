package org.fightjc.xybot.util;

import org.fightjc.xybot.dao.GroupSwitchDao;
import org.fightjc.xybot.pojo.GroupSwitch;
import org.fightjc.xybot.pojo.GroupSwitchRecord;
import org.fightjc.xybot.pojo.ResultOutput;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * 机器人功能开关管理器
 */
public class BotSwitch {

    /**
     * 记录所有功能和默认开关
     */
    private Map<String, Boolean> switchList;

    @Autowired
    private GroupSwitchDao groupSwitchDao;

    private BotSwitch() {
        switchList = new HashMap<>();
    }

    private static class Lazy {
        private static final BotSwitch instance = new BotSwitch();
    }

    public static final BotSwitch getInstance() { return BotSwitch.Lazy.instance; }

    public void registerSwitch(String name, Boolean isAutoOn) {
        switchList.put(name, isAutoOn);
    }

    /**
     * 开启指定功能
     * @param name
     * @return
     */
    public ResultOutput<String> open(Long groupId, String name, Long modifiedUserId) {
        if (switchList.containsKey(name)) {
            // 更新数据库
            GroupSwitch groupSwitch = new GroupSwitch(groupId, name, true);
            groupSwitchDao.updateGroupSwitch(groupSwitch);
            groupSwitchDao.createGroupSwitchRecord(new GroupSwitchRecord(groupSwitch, modifiedUserId));
            return new ResultOutput<>(true, "[" + name + "] 已开启");
        } else {
            return new ResultOutput<>(false, "[" + name + "] 功能不存在");
        }
    }

    /**
     * 关闭指定功能
     * @param name
     * @return
     */
    public ResultOutput<String> close(Long groupId, String name, Long modifiedUserId) {
        if (switchList.containsKey(name)) {
            // 更新数据库
            GroupSwitch groupSwitch = new GroupSwitch(groupId, name, false);
            groupSwitchDao.updateGroupSwitch(groupSwitch);
            groupSwitchDao.createGroupSwitchRecord(new GroupSwitchRecord(groupSwitch, modifiedUserId));
            return new ResultOutput<>(true, "[" + name + "] 已关闭");
        } else {
            return new ResultOutput<>(false, "[" + name + "] 功能不存在");
        }
    }

    /**
     * 列出所有已注册功能的开关状态
     * @return
     */
    public String getList(Long groupId) {
        String result = "";
        List<GroupSwitch> groupSwitchList = groupSwitchDao.getAllGroupSwitches(groupId);
        for (String key : switchList.keySet()) {
            GroupSwitch groupSwitch = groupSwitchList.stream()
                    .filter(gs -> gs.getName().equals(key))
                    .findFirst()
                    .orElse(null);
            if (groupSwitch == null) {
                result += key + " " + (switchList.getOrDefault(key, false) ? "开启中" : "未开启") + "\n";
            } else {
                result += groupSwitch.getName() + " " + (groupSwitch.isOn() ? "开启中" : "未开启") + "\n";
            }
        }
        if (result.length() == 0) {
            return "没有加载任何功能！";
        } else {
            return result.substring(0, result.length() - 1);
        }
    }

    /**
     * 检查指定功能是否开启
     * @param name
     * @return
     */
    public boolean check(Long groupId, String name) {
        GroupSwitch groupSwitch = groupSwitchDao.getGroupSwitch(groupId, name);
        if (groupSwitch == null) {
            boolean status = switchList.getOrDefault(name, false);

            // 插入默认值
            GroupSwitch temp = new GroupSwitch(groupId, name, status);
            groupSwitchDao.createGroupSwitch(temp);
            groupSwitchDao.createGroupSwitchRecord(new GroupSwitchRecord(temp, 0L));

            return status;
        } else {
            return groupSwitch.isOn();
        }
    }

}
