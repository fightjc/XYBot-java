package org.fightjc.xybot.util;

import org.fightjc.xybot.pojo.Gacha;

import java.util.ArrayList;
import java.util.HashMap;
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
    ArrayList<Long> gachaRecord;

    /**
     * 标记当天
     */
    String currentDateString;

    public BotGacha(Map<Gacha, Integer> items) {
        this.items = items;
        this.sum = items.values().stream().mapToInt(x -> x).sum();
        this.gachaRecord = new ArrayList<>();
        this.currentDateString = MessageUtil.getCurrentDate();
    }

    /**
     * 抽签并返回抽签结果
     * @param id
     * @return
     */
    public String getGacha(Long id) {
        // 当天一个id只能做一次抽签
        if (checkHasGacha(id)) {
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

    /**
     * 判断是否已经抽签
     * @param id
     * @return
     */
    private boolean checkHasGacha(Long id) {
        String tempDateString = MessageUtil.getCurrentDate();
        if (!tempDateString.equals(currentDateString)) {
            currentDateString = tempDateString;
            gachaRecord.clear();
        }
        boolean result = gachaRecord.contains(id);
        if (!result) gachaRecord.add(id);
        return result;
    }

    /**
     * 获取签概率
     * @param id
     * @return
     */
    private int getGachaRate(Long id) {
        String seed = id + MessageUtil.getCurrentDate();
        return Math.abs(Md5Util.bytes2Int(Md5Util.getMd5Byte(seed))) % sum;
    }
}
