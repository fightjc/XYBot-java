package org.fightjc.xybot.db;

import org.apache.http.util.TextUtils;
import org.fightjc.xybot.dao.BotDBDao;
import org.fightjc.xybot.po.DBMigrationTable;
import org.fightjc.xybot.pojo.BotDB;
import org.fightjc.xybot.util.MessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class DBMigration {

    private static final Logger logger = LoggerFactory.getLogger(DBMigration.class);

    @Autowired
    BotDBDao dbDao;

    @Autowired
    @Qualifier("dbMigrationTableList")
    List<DBMigrationTable> dbMigrationTableList;

    public void updateDB() {
        BotDB currentVersionInfo = dbDao.getCurrentVersionInfo();
        int currentVersion = 0;
        if (currentVersionInfo != null) {
            currentVersion = currentVersionInfo.getVersion();
        }
        logger.info("准备同步数据库，当前版本为 " + currentVersion);

        if (dbMigrationTableList.isEmpty()) {
            logger.info("获取所有数据库版本列表为空.");
            return;
        }

        // 从低版本开始比较是否需要升级数据库
        dbMigrationTableList.sort(Comparator.naturalOrder());
        for (DBMigrationTable table : dbMigrationTableList) {
            int migrationId = table.getVersion();
            if (migrationId > currentVersion) {
                logger.info("即将同步数据库，版本为 " + migrationId);
                for (String sql : table.getSqlList()) {
                    if (TextUtils.isEmpty(sql)) {
                        continue;
                    }
                    logger.info(sql);
                    dbDao.runDDLSql(sql);
                }
                logger.info("更新记录同步到数据库");
                BotDB newVersion = new BotDB(table.getRemark(), migrationId, MessageUtil.getCurrentDateTime());
                dbDao.createMigrationHistory(newVersion);
                logger.info("已更新数据库到版本 " + migrationId);
            }
        }

        logger.info("同步数据库完成.");
    }
}
