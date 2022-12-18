package org.fightjc.xybot.module.genshin.pojo;

import java.awt.image.BufferedImage;
import java.util.List;

public class WeaponDrawDto {
    BufferedImage awakenIcon;
    WeaponBean info;
    CostDto mora;
    List<CostDto> avatarAscend;
    List<CostDto> weaponAscend;

    public WeaponDrawDto(BufferedImage awakenIcon, WeaponBean info, CostDto mora, List<CostDto> avatarAscend,
                         List<CostDto> weaponAscend) {
        this.awakenIcon = awakenIcon;
        this.info = info;
        this.mora = mora;
        this.avatarAscend = avatarAscend;
        this.weaponAscend = weaponAscend;
    }

    public BufferedImage getAwakenIcon() {
        return awakenIcon;
    }

    public WeaponBean getInfo() {
        return info;
    }

    public CostDto getMora() {
        return mora;
    }

    public List<CostDto> getAvatarAscend() {
        return avatarAscend;
    }

    public List<CostDto> getWeaponAscend() {
        return weaponAscend;
    }
}
