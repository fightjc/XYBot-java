package org.fightjc.xybot.pojo.bilibili;

import java.awt.image.BufferedImage;

public class DynamicDto {
    String uid;
    String uname;
    BufferedImage faceImage;
    BufferedImage pendantImage;

    String dynamicId;
    String dateString;
    int type;

    public DynamicDto(String uid, String uname, BufferedImage faceImage, BufferedImage pendantImage,
                      String dynamicId, String dateString, int type) {
        this.uid = uid;
        this.uname = uname;
        this.faceImage = faceImage;
        this.pendantImage = pendantImage;

        this.dynamicId = dynamicId;
        this.dateString =dateString;
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public String getUname() {
        return uname;
    }

    public BufferedImage getFaceImage() {
        return faceImage;
    }

    public BufferedImage getPendantImage() {
        return pendantImage;
    }

    public String getDynamicId() {
        return dynamicId;
    }

    public String getDateString() {
        return dateString;
    }

    public int getType() {
        return type;
    }

}
