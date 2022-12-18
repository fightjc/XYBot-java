package org.fightjc.xybot.module.genshin.pojo;

import java.awt.image.BufferedImage;
import java.util.List;

public class CharacterDrawDto {
    BufferedImage image;
    CharacterBean info;

    // 角色突破
    CostDto mora;
    List<CostDto> avatarAscend;
    List<CostDto> exchangeAscend;

    // 天赋突破
    CostDto talent_mora;
    List<CostDto> talent_avatarAscend;
    List<CostDto> talentAscend;

    public CharacterDrawDto(BufferedImage image, CharacterBean info,
                            CostDto mora, List<CostDto> avatarAscend, List<CostDto> exchangeAscend,
                            CostDto talent_mora, List<CostDto> talent_avatarAscend, List<CostDto> talentAscend) {
        this.image = image;
        this.info = info;
        this.mora = mora;
        this.avatarAscend = avatarAscend;
        this.exchangeAscend = exchangeAscend;
        this.talent_mora = talent_mora;
        this.talent_avatarAscend = talent_avatarAscend;
        this.talentAscend = talentAscend;
    }

    public BufferedImage getImage() {
        return image;
    }

    public CharacterBean getInfo() {
        return info;
    }

    public CostDto getMora() {
        return mora;
    }

    public List<CostDto> getAvatarAscend() {
        return avatarAscend;
    }

    public List<CostDto> getExchangeAscend() {
        return exchangeAscend;
    }

    public CostDto getTalent_mora() {
        return talent_mora;
    }

    public List<CostDto> getTalent_avatarAscend() {
        return talent_avatarAscend;
    }

    public List<CostDto> getTalentAscend() {
        return talentAscend;
    }
}