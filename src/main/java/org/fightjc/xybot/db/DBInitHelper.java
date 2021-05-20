package org.fightjc.xybot.db;

public class DBInitHelper {

    private DBInitHelper() {}

    private static class Lazy {
        private static final DBInitHelper instance = new DBInitHelper();
    }

    public static final DBInitHelper getInstance() {
        return Lazy.instance;
    }
}
