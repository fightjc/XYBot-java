package org.fightjc.xybot.pojo.bilibili;

public class UserInfoDto {
    String mid;
    String name;
    String sex;
    String face;
    String sign;

    public UserInfoDto(String mid, String name, String sex, String face, String sign) {
        this.mid = mid;
        this.name = name;
        this.sex = sex;
        this.face = face;
        this.sign = sign;
    }

    public String getMid() {
        return mid;
    }

    public String getName() {
        return name;
    }

    public String getSex() {
        return sex;
    }

    public String getFace() {
        return face;
    }

    public String getSign() {
        return sign;
    }
}
