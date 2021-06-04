package org.fightjc.xybot.dao;

import org.apache.ibatis.annotations.Param;
import org.fightjc.xybot.pojo.BotDB;
import org.springframework.stereotype.Repository;

@Repository
public interface BotDBDao {

    /**
     * 获取当前数据库版本信息
     * @return
     */
    BotDB getCurrentVersionInfo();

    /**
     * 插入一条新版本更新数据
     * @param db
     * @return
     */
    int createMigrationHistory(BotDB db);

    /**
     * 直接执行ddl sql
     * @param sql
     * @return
     */
    int runDDLSql(@Param("sql") String sql);
}
