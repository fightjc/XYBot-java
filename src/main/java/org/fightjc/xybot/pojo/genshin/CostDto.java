package org.fightjc.xybot.pojo.genshin;

import java.awt.image.BufferedImage;

public class CostDto {
    String name;
    int count;
    BufferedImage image;
    int sort;
    String info;

    public CostDto(String name, int count, BufferedImage image, int sort, String info) {
        this.name = name;
        this.count = count;
        this.image = image;
        this.sort = sort;
        this.info = info;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public BufferedImage getImage() {
        return image;
    }

    public int getSort() {
        return sort;
    }

    public String getInfo() {
        return info;
    }
}
