package org.fightjc.xybot.pojo.bilibili;

import java.awt.image.BufferedImage;

public class DynamicPictureBean {
    int imageWidth;
    int imageHeight;
    BufferedImage image;

    public DynamicPictureBean(int imageWidth, int imageHeight, BufferedImage image) {
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.image = image;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public BufferedImage getImage() {
        return image;
    }
}