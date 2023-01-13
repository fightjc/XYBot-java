package org.fightjc.xybot.module.bot;

import org.fightjc.xybot.enums.ResultCode;
import org.fightjc.xybot.model.dto.ResultOutput;

import java.util.HashMap;
import java.util.Map;

/**
 * 机器人功能开关管理器
 */
public class BotSwitch {

    /**
     * 记录所有功能和默认开关
     */
    private Map<String, Boolean> switchList;

    /**
     * 记录群功能和开关
     */
    private Map<String, Boolean> groupSwitchList;

    public BotSwitch() {
        switchList = new HashMap<>();
        groupSwitchList = new HashMap<>();
    }

    private static class Lazy {
        private static final BotSwitch instance = new BotSwitch();
    }

    public static BotSwitch getInstance() { return Lazy.instance; }

    public void registerSwitch(String name, Boolean isAutoOn) {
        switchList.put(name, isAutoOn);
    }

    /**
     * 获取指定功能开关选项
     * @param groupId
     * @param name
     * @return
     */
    public ResultOutput<Boolean> getGroupSwitchStatus(Long groupId, String name) {
        String key = getGroupSwitchMapKey(groupId, name);
        if (groupSwitchList.containsKey(key)) {
            return new ResultOutput<>(ResultCode.SUCCESS, groupSwitchList.get(key));
        } else {
            return getSwitchDefaultValue(name);
        }
    }

    /**
     * 获取指定功能默认开关选项
     * @param name
     * @return
     */
    public ResultOutput<Boolean> getSwitchDefaultValue(String name) {
        if (switchList.containsKey(name)) {
            return new ResultOutput<>(ResultCode.SUCCESS, switchList.getOrDefault(name, false));
        } else {
            return new ResultOutput<>(ResultCode.FAILED, "[" + name + "] 功能不存在", false);
        }
    }

    /**
     * 列出所有已注册功能的开关状态
     * @return
     */
    public Map<String, Boolean> getSwitchList() {
        return switchList;
    }

    /**
     * 更新群功能开关列表
     * @param groupId
     * @param name
     * @param isOn
     * @return
     */
    public ResultOutput<String> createOrUpdateGroupSwitch(Long groupId, String name, boolean isOn) {
        String key = getGroupSwitchMapKey(groupId, name);
        if (groupSwitchList.containsKey(key)) {
            groupSwitchList.replace(key, isOn);
        } else {
            groupSwitchList.put(key, isOn);
        }

        return new ResultOutput<>(ResultCode.SUCCESS, "");
    }

    private String getGroupSwitchMapKey(Long groupId, String name) {
        return name + groupId.toString();
    }
}
