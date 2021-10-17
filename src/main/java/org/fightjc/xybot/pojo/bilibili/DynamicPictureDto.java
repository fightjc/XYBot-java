package org.fightjc.xybot.pojo.bilibili;

import java.awt.image.BufferedImage;
import java.util.List;

public class DynamicPictureDto extends DynamicDto {
    String description;

    List<DynamicPictureBean> imageList;

    public DynamicPictureDto(String uid, String uname, BufferedImage faceImage, BufferedImage pendantImage,
                             String dynamicId, String dateString, int type,
                             String description, List<DynamicPictureBean> imageList) {
        super(uid, uname, faceImage, pendantImage, dynamicId, dateString, type);

        this.description = description;

        this.imageList = imageList;
    }

    public String getDescription() {
        return description;
    }

    public List<DynamicPictureBean> getImageList() {
        return imageList;
    }
}
