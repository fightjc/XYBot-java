package org.fightjc.xybot.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.fightjc.xybot.pojo.Gacha;
import org.fightjc.xybot.util.BotUtil;
import org.fightjc.xybot.util.ImageUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class GenshinImagesConfig {

    @Bean(name = "roleImageMap")
    public Map<String, BufferedImage> getRoleImageMap() {
        return getImageMap("role");
    }

    @Bean(name = "materialImageMap")
    public Map<String, BufferedImage> getMaterialImageMap() {
        return getImageMap("material");
    }

    @Bean(name = "weaponImageMap")
    public Map<String, BufferedImage> getWeaponImageMap() {
        return getImageMap("weapon");
    }

    /**
     * 读取雪碧图并切割保存为字典对象
     * @param component
     * @return
     */
    private Map<String, BufferedImage> getImageMap(String component) {
        Map<String, BufferedImage> imageMap = new HashMap<>();

        // 获取图片
        String pngPath = BotUtil.getResourceFolderPath() + "/" + component + ".png";
        BufferedImage rawImage = BotUtil.readImageFile(pngPath);
        if (rawImage == null) return imageMap;

        // 获取json
        String jsonPath = BotUtil.getResourceFolderPath() + "/" + component + ".json";
        JSONObject jsonObject = BotUtil.readJsonFile(jsonPath);
        if (jsonObject == null) return imageMap;

        JSONArray jsonArray = jsonObject.getJSONArray(component);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            String name = object.getString("name");
            Integer x = object.getInteger("x");
            Integer y = object.getInteger("y");
            Integer w = object.getInteger("w");
            Integer h = object.getInteger("h");
            imageMap.put(name, rawImage.getSubimage(x, y, w, h));
        }

        return imageMap;
    }
}
