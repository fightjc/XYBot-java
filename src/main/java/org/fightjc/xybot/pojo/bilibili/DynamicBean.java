package org.fightjc.xybot.pojo.bilibili;

public class DynamicBean {
    private Integer id;

    private String mid;

    private String name;

    private Long follower;

    private String offset;

    public DynamicBean(String mid, String name, Long follower, String offset) {
        this.mid = mid;
        this.name = name;
        this.follower = follower;
        this.offset = offset;
    }

    public String getMid() {
        return mid;
    }

    public String getName() {
        return name;
    }

    public Long getFollower() {
        return follower;
    }

    public String getOffset() {
        return offset;
    }

    public void changeSubscribe(boolean isFollow) {
        follower += isFollow ? 1 : -1;
    }
}
