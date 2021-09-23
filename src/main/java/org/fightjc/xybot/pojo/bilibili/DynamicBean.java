package org.fightjc.xybot.pojo.bilibili;

public class DynamicBean {
    private Integer id;

    private String mid;

    private String name;

    private String offset;

    public DynamicBean(String mid, String name, String offset) {
        this.mid = mid;
        this.name = name;
        this.offset = offset;
    }

    public String getMid() {
        return mid;
    }

    public String getName() {
        return name;
    }

    public String getOffset() {
        return offset;
    }
}
