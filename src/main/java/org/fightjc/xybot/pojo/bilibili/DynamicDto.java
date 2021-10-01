package org.fightjc.xybot.pojo.bilibili;

import net.mamoe.mirai.utils.ExternalResource;

import java.util.List;

public class DynamicDto {
    String dynamicId;
    int type;
    String description;
    List<ExternalResource> imageList;

    public DynamicDto(String dynamicId, int type, String description, List<ExternalResource> imageList) {
        this.dynamicId = dynamicId;
        this.type = type;
        this.description = description;
        this.imageList = imageList;
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
