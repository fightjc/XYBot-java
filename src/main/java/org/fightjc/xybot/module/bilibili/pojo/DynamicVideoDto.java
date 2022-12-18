package org.fightjc.xybot.module.bilibili.pojo;

import java.awt.image.BufferedImage;

public class DynamicVideoDto extends DynamicDto {
    String description;

    String videoShortLink;
    BufferedImage videoPic;
    String videoTitle;
    int videoViewNum;
    int videoLikeNum;
    int videoCoinNum;
    int videoFavoriteNum;

    public DynamicVideoDto(String uid, String uname, BufferedImage faceImage, BufferedImage pendantImage,
                           String dynamicId, String dateString, int type,
                           String description, String videoShortLink, BufferedImage videoPic, String videoTitle,
                           int videoViewNum, int videoLikeNum, int videoCoinNum, int videoFavoriteNum) {
        super(uid, uname, faceImage, pendantImage, dynamicId, dateString, type);

        this.description = description;

        this.videoShortLink = videoShortLink;
        this.videoPic = videoPic;
        this.videoTitle = videoTitle;
        this.videoViewNum = videoViewNum;
        this.videoLikeNum = videoLikeNum;
        this.videoCoinNum = videoCoinNum;
        this.videoFavoriteNum = videoFavoriteNum;
    }

    public String getDescription() {
        return description;
    }

    public String getVideoShortLink() {
        return videoShortLink;
    }

    public BufferedImage getVideoPic() {
        return videoPic;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public int getVideoViewNum() {
        return videoViewNum;
    }

    public int getVideoLikeNum() {
        return videoLikeNum;
    }

    public int getVideoCoinNum() {
        return videoCoinNum;
    }

    public int getVideoFavoriteNum() {
        return videoFavoriteNum;
    }

}
