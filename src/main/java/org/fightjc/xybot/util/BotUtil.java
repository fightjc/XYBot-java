package org.fightjc.xybot.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class BotUtil {

    private static final Logger logger = LoggerFactory.getLogger(BotUtil.class);

    public static String workPath = "";

    public static String rFolderName = "resources";

    public static String gFolderName = "genshin";

    public static String dbFileName = "botData.db";

    public static String gachaFileName = "gacha.json";

    public static String getWorkPath() {
        if (!workPath.isEmpty()) {
            return workPath;
        } else {
            String path = BotUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            if (System.getProperty("os.name").contains("dows")) {
                path = path.substring(1);
            }
            if (path.contains("jar")) {
                path = path.substring(0, path.lastIndexOf("."));
                path = path.substring(0, path.lastIndexOf("/") + 1);
            }
            path = path.replace("target/classes/", "").replace("file:", "");
            return path;
        }
    }

    public static String getResourceFolderPath() {
        return getWorkPath() + rFolderName;
    }

    public static String getGenshinFolderPath() {
        return getResourceFolderPath() + "/" + gFolderName;
    }

    public static String getDBFilePath() {
        return getWorkPath() + dbFileName;
    }

    public static String getGachaFilePath() {
        return getResourceFolderPath() + "/" + gachaFileName;
    }

    /**
     * 获取JSON文件内容
     * @param filePath 文件路径
     * @return JSONObject对象
     */
    public static JSONObject readJsonFile(String filePath) {
        // 读取文件内容
        String readJson = getTextFile(filePath);

        // 获取json
        try {
            return JSONObject.parseObject(readJson);
        } catch (JSONException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    /**
     * 获取JSON文件内容
     * @param filePath 文件路径
     * @param clazz 反序列化对象Class
     * @param <T> 反序列化对象范型
     * @return 反序列化对象
     */
    public static <T> T readJsonFile(String filePath, Class<T> clazz) {
        // 读取文件内容
        String readJson = getTextFile(filePath);

        // 获取json
        try {
            return JSON.parseObject(readJson, clazz);
        } catch (JSONException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    /**
     * 获取Text文件内容
     * @param filePath 文件路径
     * @return 文件文本内容
     */
    public static String getTextFile(String filePath) {
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();

        // 读取文件内容
        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
            reader = new BufferedReader(inputStreamReader);
            String tempString;
            while ((tempString = reader.readLine()) != null) {
                content.append(tempString);
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e){
                    logger.error(e.getMessage());
                }
            }
        }
        return content.toString();
    }

    /**
     * 读取Image文件内容
     * @param filePath 文件路径
     * @return 图片对象
     */
    public static BufferedImage readImageFile(String filePath) {
        File pngFile = new File(filePath);
        try {
            BufferedImage image = ImageIO.read(pngFile);
            return image;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
