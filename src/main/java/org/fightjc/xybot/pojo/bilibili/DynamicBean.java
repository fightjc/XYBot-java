package org.fightjc.xybot.pojo.bilibili;

public class DynamicBean {
    private Integer id;

    private String mid;

    private String offset;

    public DynamicBean(String mid, String offset) {
        this.mid = mid;
        this.offset = offset;
    }

    public String getOffset() {
        return offset;
    }
}
