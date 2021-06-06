package org.fightjc.xybot.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.fightjc.xybot.pojo.Gacha;
import org.fightjc.xybot.pojo.ResultOutput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BotGacha {

    /**
     * 所有签内容和权重
     */
    private Map<Gacha, Integer> items;

    /**
     * 总值
     */
    private int sum;

    /**
     * 标记已经进行过抽签的人Id
     */
    private ArrayList<Long> gachaRecord;

    private BotGacha() {
        this.items = new HashMap<>();
        this.sum = 0;
        this.gachaRecord = new ArrayList<>();
    }

    private static class Lazy {
        private static final BotGacha instance = new BotGacha();
    }

    public static final BotGacha getInstance() {
        return BotGacha.Lazy.instance;
    }

    /**
     * 加载签信息
     */
    public void reloadItems() {
        items.clear();
        gachaRecord.clear();

        String gcFilePath = BotUtil.getGachaFilePath();
        JSONObject jsonObject = BotUtil.readJsonFile(gcFilePath);
        JSONArray gachaArray = jsonObject.getJSONArray("gacha");
        for (int i = 0; i < gachaArray.size(); i++) {
            JSONObject gacha = gachaArray.getJSONObject(i);
            String title = gacha.getString("title");
            String content = gacha.getString("content");
            Integer weight = gacha.getInteger("weight");
            items.put(new Gacha(title, content), weight);
        }

        sum = items.values().stream().mapToInt(x -> x).sum();
    }

    /**
     * 抽签并返回抽签结果
     * @param id
     * @return
     */
    public ResultOutput getGacha(Long id) {
        // 当天一个id只能做一次抽签
        if (checkHasGacha(id)) {
            return new ResultOutput(false, "你今天已经抽过签了，欢迎明天再来~");
        }
        gachaRecord.add(id);

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
        return new ResultOutput(true, "你抽到了 " + result.getTitle() + "\n" + result.getContent());
    }

    /**
     * 清除已抽签记录
     */
    public void clearRecord() {
        gachaRecord.clear();
    }

    /**
     * 判断是否已经抽签
     * @param id
     * @return
     */
    private boolean checkHasGacha(Long id) {
        return gachaRecord.contains(id);
    }

    /**
     * 获取签概率
     * @param id
     * @return
     */
    private int getGachaRate(Long id) {
        String seed = id + MessageUtil.getCurrentDateTime();
        if (sum != 0) {
            return Math.abs(Md5Util.bytes2Int(Md5Util.getMd5Byte(seed))) % sum;
        } else {
            return 0;
        }
    }
}
