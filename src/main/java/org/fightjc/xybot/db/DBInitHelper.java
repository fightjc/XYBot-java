package org.fightjc.xybot.db;

import org.fightjc.xybot.util.BotUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DBInitHelper {

    private static final Logger logger = LoggerFactory.getLogger(DBInitHelper.class);

    private DBInitHelper() {}

    private static class Lazy {
        private static final DBInitHelper instance = new DBInitHelper();
    }

    public static final DBInitHelper getInstance() {
        return Lazy.instance;
    }

    /**
     * 加载数据库文件
     */
    public void prepareDB() {
        String dbFilePath = BotUtil.getDBFilePath();
        File dbFile = new File(dbFilePath);
        if (dbFile.exists()) {
            logger.info("数据库文件已找到.");
        } else {
            logger.info("找不到数据库文件，即将创建...");
            if (createDB()) {
                logger.info("创建数据库文件成功: " + dbFilePath);
            } else {
                logger.info("创建数据库文件失败: " + dbFilePath);
            }
        }
    }

    /**
     * 创建初始版本数据库
     * @return
     */
    private boolean createDB() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ce) {
            logger.error(ce.getMessage());
            return false;
        }

        String url = "jdbc:sqlite:" + BotUtil.getDBFilePath();
        try {
            Connection conn = DriverManager.getConnection(url);
            Statement stat = conn.createStatement();

            // 创建数据库版本控制历史表
            stat.executeUpdate("DROP TABLE IF EXISTS __migrationhistory;");
            stat.executeUpdate("create table __migrationhistory(" +
                    "Id INTEGER PRIMARY KEY autoincrement," +
                    "Remark varchar(255)," +
                    "Version integer," +
                    "ExecuteTime varchar(20));"
            );

            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }
}
