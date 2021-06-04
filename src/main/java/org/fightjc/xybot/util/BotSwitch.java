package org.fightjc.xybot.util;

import org.fightjc.xybot.pojo.ResultOutput;

import java.util.HashMap;
import java.util.Map;

/**
 * 机器人功能开关管理器
 */
public class BotSwitch {

    private static final Map<String, Boolean> switchList = new HashMap<>();

    public static void init(String name, Boolean isOn) {
        switchList.put(name, isOn);
    }

    /**
     * 开启指定功能
     * @param name
     * @return
     */
    public static ResultOutput<String> open(String name) {
        if (switchList.containsKey(name)) {
            switchList.put(name, true);
            return new ResultOutput<>(true, "[" + name + "]已开启");
        } else {
            return new ResultOutput<>(false, "[" + name + "]功能不存在");
        }
    }

    /**
     * 关闭指定功能
     * @param name
     * @return
     */
    public static ResultOutput<String> close(String name) {
        if (switchList.containsKey(name)) {
            switchList.put(name, false);
            return new ResultOutput<>(true, "[" + name + "]已关闭");
        } else {
            return new ResultOutput<>(false, "[" + name + "]功能不存在");
        }
    }

    /**
     * 列出所有已注册功能的开关状态
     * @return
     */
    public static String getList() {
        String result = "";
        for (String key : switchList.keySet()) {
            result += key + " " + (check(key) ? "开启中" : "未开启") + "\n";
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
    public static boolean check(String name) {
        return switchList.getOrDefault(name, false);
    }
}
