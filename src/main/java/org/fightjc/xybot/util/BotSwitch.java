package org.fightjc.xybot.util;

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

}
