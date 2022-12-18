package org.fightjc.xybot.module.genshin.pojo;

public class CostBean {
    String name;
    int count;

    public CostBean(String name, int count) {
        this.name = name;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }
}
