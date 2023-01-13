package org.fightjc.xybot.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.fightjc.xybot.model.DBMigrationTable;
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
import java.util.UUID;

@Configuration
@MapperScan(basePackages = { "org.fightjc.xybot.dao", "org.fightjc.xybot.module.**.dao" })
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
        tables.add(migration_v3());
        tables.add(migration_v4());

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

        String create_BiliGroupSubscribe =
                "create table bili_subscribe(" +
                        "Id INTEGER PRIMARY KEY autoincrement," +
                        "GroupId integer," +
                        "Mid varchar(50)," +
                        "IsActive varchar(1));";
        sqlList.add(create_BiliGroupSubscribe);

        String create_BiliGroupSubscribeRecord =
            "create table bili_subscribeRecord(" +
                    "Id INTEGER PRIMARY KEY autoincrement," +
                    "GroupId integer," +
                    "Mid varchar(50)," +
                    "IsActive varchar(1)," +
                    "ModifiedUserId integer," +
                    "ModifiedTime varchar(20));";
        sqlList.add(create_BiliGroupSubscribeRecord);

        String create_BiliDynamic =
                "create table bili_dynamic(" +
                        "Id INTEGER PRIMARY KEY autoincrement," +
                        "Mid varchar(50)," +
                        "Name varchar(100)," +
                        "Follower integer," +
                        "Offset varchar(100));";
        sqlList.add(create_BiliDynamic);

        return new DBMigrationTable(2, remark, sqlList);
    }

    /**
     * add tables for genshin calendar subscribe
     */
    private DBMigrationTable migration_v3() {
        String remark = "addGenshinCalendar";
        List<String> sqlList = new ArrayList<>();

        String create_GenshinCalendar =
                "create table genshin_calendar(" +
                        "Id INTEGER PRIMARY KEY autoincrement," +
                        "GroupId integer," +
                        "IsActive varchar(1));";
        sqlList.add(create_GenshinCalendar);

        String create_GenshinCalendarRecord =
                "create table genshin_calendarRecord(" +
                        "Id INTEGER PRIMARY KEY autoincrement," +
                        "GroupId integer," +
                        "IsActive varchar(1)," +
                        "ModifiedUserId integer," +
                        "ModifiedTime varchar(20));";
        sqlList.add(create_GenshinCalendarRecord);

        return new DBMigrationTable(3, remark, sqlList);
    }

    /**
     * add tables for users & user roles
     */
    private DBMigrationTable migration_v4() {
        String remark = "addUserLogin";
        List<String> sqlList = new ArrayList<>();

        String create_User =
                "create table User(" +
                        "Id varchar(36) PRIMARY KEY," +
                        "Username varchar(100)," +
                        "Password varchar(100)," +
                        "Name varchar(100)," +
                        "Email varchar(100)," +
                        "CreationTime varchar(20)," +
                        "Active varchar(1)," +
                        "DeletionTime varchar(20))";
        sqlList.add(create_User);

        // preset Guid
        String userId = UUID.randomUUID().toString();
        String adminRoleId = UUID.randomUUID().toString();
        String userRoleId = UUID.randomUUID().toString();

        String insert_User =
                "insert into User(Id, Username, Password, Name, Active)" +
                        "values(\"" + userId + "\", \"xybot\", \"$2a$12$iNyi7G570wZ6bfXIiIuDp.uxmWRrXJ5tw839jEZ77T884EbYdIyRy\", \"xybot\", 1)";
        sqlList.add(insert_User);

        String create_Role =
                "create table Role(" +
                        "Id varchar(36) PRIMARY KEY," +
                        "Name varchar(100)," +
                        "Remark varchar(200)," +
                        "IsDefault varchar(1))";
        sqlList.add(create_Role);

        String insert_Admin_Role =
                "insert into Role(Id, Name, Remark, IsDefault)" +
                        "values(\"" + adminRoleId + "\", \"管理员\", \"\",  0)";
        sqlList.add(insert_Admin_Role);

        String insert_User_Role =
                "insert into Role(Id, Name, Remark, IsDefault)" +
                        "values(\"" + userRoleId + "\", \"用户\", \"\", 1)";
        sqlList.add(insert_User_Role);

        String create_UserRole =
                "create table UserRole(" +
                        "UserId varchar(36) PRIMARY KEY," +
                        "RoleId varchar(36))";
        sqlList.add(create_UserRole);

        String insert_UserRole =
                "insert into UserRole(UserId, RoleId)" +
                        "values(\"" + userId + "\", \"" + adminRoleId + "\")";
        sqlList.add(insert_UserRole);

//        String create_RolePermission = "";
//        sqlList.add(create_RolePermission);

        return new DBMigrationTable(4, remark, sqlList);
    }
}
