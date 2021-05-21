package org.fightjc.xybot.util;

public class BotUtil {

    public static String dbFileName = "botData.db";

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

    public static String getDBFilePath() {
        return getWorkPath() + dbFileName;
    }
}
