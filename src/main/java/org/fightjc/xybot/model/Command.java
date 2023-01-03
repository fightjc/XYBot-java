package org.fightjc.xybot.model;

import java.util.ArrayList;
import java.util.Arrays;

public class Command {

    // 呼叫指令文本
    String name;

    ArrayList<String> alias;

    public Command(String name, ArrayList<String> alias) {
        this.name = name;
        this.alias = alias;
    }

    public Command(String name, String ... alias) {
        this(name, new ArrayList<>(Arrays.asList(alias)));
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getAlias() {
        return alias;
    }
}
