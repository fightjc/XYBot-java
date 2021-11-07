package org.fightjc.xybot.pojo.genshin;

import java.awt.image.BufferedImage;

public class WeaponDrawDto {
    BufferedImage awakenIcon;
    WeaponBean info;

    public WeaponDrawDto(BufferedImage awakenIcon, WeaponBean info) {
        this.awakenIcon = awakenIcon;
        this.info = info;
    }

    public BufferedImage getAwakenIcon() {
        return awakenIcon;
    }

    public WeaponBean getInfo() {
        return info;
    }
}
