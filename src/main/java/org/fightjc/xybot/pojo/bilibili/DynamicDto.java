package org.fightjc.xybot.pojo.bilibili;

import net.mamoe.mirai.utils.ExternalResource;

import java.util.List;

public class DynamicDto {
    String uid;
    String uname;
    String dateString;

    String dynamicId;
    int type;
    String description;
    List<ExternalResource> imageList;

    public DynamicDto(String uid, String uname, String dateString, String dynamicId, int type, String description,
                      List<ExternalResource> imageList) {
        this.uid = uid;
        this.uname = uname;
        this.dateString =dateString;
        this.dynamicId = dynamicId;
        this.type = type;
        this.description = description;
        this.imageList = imageList;
    }

    public String getUid() {
        return uid;
    }

    public String getUname() {
        return uname;
    }

    public String getDateString() {
        return dateString;
    }

    public String getDynamicId() {
        return dynamicId;
    }

    public int getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public List<ExternalResource> getImageList() {
        return imageList;
    }
}
