package org.fightjc.xybot.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.fightjc.xybot.po.DBMigrationTable;
import org.fightjc.xybot.util.BotUtil;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Configuration
@MapperScan(basePackages = "org.fightjc.xybot.dao")
public class DBMigrationConfig {

    @Autowired
    DataSource dataSource;

    @Bean(name="sqliteDataSource")
    public DataSource sqliteDataSource() {
        SQLiteConnectionPoolDataSource pool = new SQLiteConnectionPoolDataSource();
        pool.setUrl("jdbc:sqlite:" + BotUtil.getDBFilePath());
        return pool;
    }

    @Bean
    public SqlSessionFactory sqliteSqlSessionFactory(@Qualifier("sqliteDataSource") DataSource dataSource) throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setMapperLocations(
                new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml"));
        return sessionFactory.getObject();
    }

    @Bean(name="dbMigrationTableList")
    public List<DBMigrationTable> getDBMigrationTables() {
        List<DBMigrationTable> tables = new ArrayList<>();

        tables.add(migration_v1());
        tables.add(migration_v2());

        return tables;
    }

    /**
     * add tables for group switch module
     */
    private DBMigrationTable migration_v1() {
        String remark = "addGroupSwitch";
        List<String> sqlList = new ArrayList<>();

        String create_GroupSwitch =
                "create table GroupSwitch(" +
                        "Id INTEGER PRIMARY KEY autoincrement," +
                        "GroupId integer," +
                        "Name varchar(255)," +
                        "IsOn varchar(1));";
        sqlList.add(create_GroupSwitch);

        String create_GroupSwitchRecord =
                "create table GroupSwitchRecord(" +
                        "Id INTEGER PRIMARY KEY autoincrement," +
                        "GroupId integer," +
                        "Name varchar(255)," +
                        "IsOn varchar(1)," +
                        "ModifiedUserId integer," +
                        "ModifiedTime varchar(20));";
        sqlList.add(create_GroupSwitchRecord);

        return new DBMigrationTable(1, remark, sqlList);
    }

    /**
     * add tables for bilibili post
     */
    private DBMigrationTable migration_v2() {
        String remark = "addBiliPost";
        List<String> sqlList = new ArrayList<>();

        String create_BiliGroupFollow =
                "create table bili_subscribe(" +
                        "Id INTEGER PRIMARY KEY autoincrement," +
                        "GroupId integer," +
                        "Mid varchar(50));";
        sqlList.add(create_BiliGroupFollow);

        String create_BiliDynamic =
                "create table bili_dynamic(" +
                        "Id INTEGER PRIMARY KEY autoincrement," +
                        "Mid varchar(50)," +
                        "Name varchar(100)," +
                        "Offset varchar(100));";
        sqlList.add(create_BiliDynamic);

        return new DBMigrationTable(2, remark, sqlList);
    }
}
