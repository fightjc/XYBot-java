package org.fightjc.xybot.pojo;

public class ResultOutput<T> {

    private boolean success;

    private String info;

    private T object;

    public ResultOutput(boolean success, String info) {
        this(success, info, null);
    }

    public ResultOutput(boolean success, String info, T object) {
        this.success = success;
        this.info = info;
        this.object = object;
    }

    public boolean getSuccess() {
        return success;
    }

    public String getInfo() {
        return info;
    }

    public T getObject() {
        return object;
    }
}
