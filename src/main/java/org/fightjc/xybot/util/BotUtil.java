package org.fightjc.xybot.util;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class BotUtil {

    private static final Logger logger = LoggerFactory.getLogger(BotUtil.class);

    public static String dbFileName = "botData.db";

    public static String rFolderName = "resources";

    public static String gachaFileName = "gacha.json";

    public static String getWorkPath() {
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

    public static String getResourceFolderPath() {
        return getWorkPath() + rFolderName;
    }

    public static String getDBFilePath() {
        return getWorkPath() + dbFileName;
    }

    public static String getGachaFilePath() {
        return getResourceFolderPath() + "/" + gachaFileName;
    }

    /**
     * 读取JSON文件并返回JSON对象
     * @param filePath
     * @return
     */
    public static JSONObject readJsonFile(String filePath){
        BufferedReader reader = null;
        String readJson = "";

        // 读取文件内容
        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
            reader = new BufferedReader(inputStreamReader);
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                readJson += tempString;
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
                    return null;
                }
            }
        }

        // 获取json
        try {
            JSONObject jsonObject = JSONObject.parseObject(readJson);
            return jsonObject;
        } catch (JSONException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    /**
     * 读取Image文件并返回BufferedImage对象
     * @param filePath
     * @return
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
