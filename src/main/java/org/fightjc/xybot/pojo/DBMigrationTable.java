package org.fightjc.xybot.pojo;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DBMigrationTable implements Comparable<DBMigrationTable> {

    int version;

    String remark;

    List<String> sqlList;

    public DBMigrationTable(int version, String remark, List<String> sqlList) {
        this.version = version;
        this.remark = remark;
        this.sqlList = sqlList;
    }

    public int getVersion() {
        return version;
    }

    public String getRemark() {
        return remark;
    }

    public List<String> getSqlList() {
        return sqlList;
    }

    @Override
    public int compareTo(@NotNull DBMigrationTable o) {
        if (this.version > o.getVersion()) {
            return 1;
        } else if (this.version < o.getVersion()) {
            return -1;
        }
        return 0;
    }
}
