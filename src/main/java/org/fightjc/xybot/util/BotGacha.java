package org.fightjc.xybot.util;

import org.fightjc.xybot.pojo.Gacha;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public class BotGacha {

    /**
     * 所有签内容和权重
     */
    Map<Gacha, Integer> items;

    /**
     * 总值
     */
    int sum;

    /**
     * 标记已经进行过抽签的人Id
     */
    ArrayList<Long> hasGachaIds;

    public BotGacha(Map<Gacha, Integer> items) {
        this.items = items;
        this.sum = items.values().stream().collect(Collectors.summingInt(x -> x));
        this.hasGachaIds = new ArrayList<>();
    }

    /**
     * 抽签并返回抽签结果
     * @param id
     * @return
     */
    public String getGasha(Long id) {
        if (hasGachaIds.contains(id)) {
            return "你今天已经抽过签了，欢迎明天再来~";
        }

        int sum = 0;
        int rate = getGachaRate(id);
        Gacha result = new Gacha("", "");
        for (Map.Entry<Gacha, Integer> item: items.entrySet()) {
            Integer weight = item.getValue();
            if (sum <= rate && rate < sum + weight) {
                result = item.getKey();
                break;
            }
            sum += weight;
        }
        return "你抽到了 " + result.getTitle() + "\n" + result.getContent();
    }

    public void clearHasGachaIds() {
        hasGachaIds.clear();
    }

    private int getGachaRate(Long id) {
        String seed = id + MessageUtil.getCurrentDate();
        return Math.abs(Md5Util.bytes2Int(Md5Util.getMd5Byte(seed))) % sum;
    }
}
