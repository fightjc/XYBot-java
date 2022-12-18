package org.fightjc.xybot.module.genshin.pojo;

public class NameMapBean {
    String name;
    String nameMap;

    public NameMapBean(String name, String nameMap) {
        this.name = name;
        this.nameMap = nameMap;
    }

    public String getName() {
        return name;
    }

    public String getNameMap() {
        return nameMap;
    }
}
