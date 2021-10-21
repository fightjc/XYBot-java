package org.fightjc.xybot.util.bilibili;

import org.fightjc.xybot.pojo.bilibili.DynamicDto;
import org.fightjc.xybot.pojo.bilibili.DynamicPictureBean;
import org.fightjc.xybot.pojo.bilibili.DynamicPictureDto;
import org.fightjc.xybot.pojo.bilibili.DynamicVideoDto;
import org.fightjc.xybot.util.BotUtil;
import org.fightjc.xybot.util.ImageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.font.FontDesignMetrics;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class BilibiliDynamicDrawHelper {

    private static final Logger logger = LoggerFactory.getLogger(BilibiliDynamicDrawHelper.class);

    private static final int IMAGE_WIDTH = 1400;        // 预设动态图片宽度
    private static final int FACE_IMAGE_WIDTH = 160;    // 头像图片宽度
    private static final int AVATAR_IMAGE_WIDTH = 260;  // 头像框图片宽度
    private static final int AVATAR_PADDING = 34;       // 内容区域上下边距
    private static final int CONTENT_MARGIN = 240;      // 内容区域左右边距
    private static final int CONTENT_PADDING = 90;      // 内容区域上下边距
    private static final int TITLE_HEIGHT = 105;        // 标题区域高度
    private static final int CONTEXT_MARGIN = 20;       // 正文与标题间距
    private static final int TEXT_1_LINE_MARGIN = 15;   // 字体T EXT_1 行间距
    private static final int TEXT_1_PARA_MARGIN = 20;   // 字体T EXT_1 段间距

    private static final int PICTURE_MARGIN = 50;       // 正文与图片间距
    private static final int PICTURE_WIDTH = 840;       // 图片宽度

    private static final int VIDEO_MARGIN = 50;         // 正文与视频信息间距
    private static final int VIDEO_IMAGE_WIDTH = 480;   // 视频图片高度

    private final static Font FONT_NAME = new Font(Font.SANS_SERIF, Font.BOLD, 42);       // up主名称
    private final static Font FONT_DATE = new Font(Font.SANS_SERIF, Font.BOLD, 28);       // 动态时间
    private final static Font TEXT_1 = new Font("Microsoft YaHei", Font.PLAIN, 30); // 正文
    private final static Font TEXT_2 = new Font("Microsoft YaHei", Font.PLAIN, 20); // 视频解释文字

    /**
     * 生成动态图片
     * @param dto
     * @return
     */
    public static BufferedImage generateDynamic(DynamicDto dto) {
        int type = dto.getType();
        switch (type) {
            case 2:
                if (dto instanceof DynamicPictureDto) {
                    return generatePicDynamic((DynamicPictureDto) dto);
                }
                break;
            case 8:
                if (dto instanceof DynamicVideoDto) {
                    return generateVideoDynamic((DynamicVideoDto) dto);
                }
                break;
        }

        return new BufferedImage(0, 0, BufferedImage.TYPE_INT_ARGB);
    }

    /**
     * 生成图片动态图片
     * @param dto
     * @return
     */
    public static BufferedImage generatePicDynamic(DynamicPictureDto dto) {
        //region 计算画板大小
        int height = CONTENT_PADDING  + TITLE_HEIGHT + CONTEXT_MARGIN;

        // 计算正文高度
        int maxWidth = IMAGE_WIDTH - 2 * CONTENT_MARGIN; // 正文最大宽度
        String desc = dto.getDescription();
        String[] descArray = desc.split("\n");
        for (String text : descArray) {
            FontMetrics metrics = FontDesignMetrics.getMetrics(TEXT_1);
            int text_height = metrics.getAscent();
            if (text.length() == 0) {
                height += text_height + TEXT_1_PARA_MARGIN;
            } else {
                int text_width = metrics.charsWidth(text.toCharArray(), 0, text.length());
                int rowCount = text_width / maxWidth + (text_width % maxWidth > 0 ? 1 : 0); // 总行数
                height += rowCount * (text_height + TEXT_1_LINE_MARGIN) - TEXT_1_LINE_MARGIN + TEXT_1_PARA_MARGIN;
            }
        }

        // 计算图片高度
        for (DynamicPictureBean bean : dto.getImageList()) {
            int w = bean.getImageWidth();
            int h = bean.getImageHeight();
            int picHeight = PICTURE_WIDTH * h / w;

            height += PICTURE_MARGIN + picHeight;
        }
        height += CONTENT_PADDING;
        //endregion

        //region 准备画板
        BufferedImage target = new BufferedImage(IMAGE_WIDTH, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = target.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // 抗锯齿
        g2d.setBackground(Color.WHITE);
        g2d.clearRect(0, 0, IMAGE_WIDTH, height);
        //endregion

        //region 绘制头像

        // 将头像设置成圆形
        BufferedImage avatarImage = new BufferedImage(FACE_IMAGE_WIDTH, FACE_IMAGE_WIDTH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D avatarImageG2d = avatarImage.createGraphics();
        Ellipse2D.Double shape = new Ellipse2D.Double(0, 0, FACE_IMAGE_WIDTH, FACE_IMAGE_WIDTH);
        avatarImageG2d.setClip(shape);
        BufferedImage faceImage = ImageUtil.getScaledImage(dto.getFaceImage(), FACE_IMAGE_WIDTH, FACE_IMAGE_WIDTH);
        avatarImageG2d.drawImage(faceImage, 0, 0, FACE_IMAGE_WIDTH, FACE_IMAGE_WIDTH, null);
        avatarImageG2d.dispose();

        // 绘制头像
        int avatarX = (CONTENT_MARGIN - FACE_IMAGE_WIDTH) / 2;
        int avatarY = AVATAR_PADDING + (AVATAR_IMAGE_WIDTH - FACE_IMAGE_WIDTH) / 2;
        g2d.drawImage(avatarImage, avatarX, avatarY, FACE_IMAGE_WIDTH, FACE_IMAGE_WIDTH, null);

        // 绘制头像框
//        BufferedImage pendantImage = ImageUtil.getScaledImage(dto.getPendantImage(), AVATAR_IMAGE_WIDTH, AVATAR_IMAGE_WIDTH);
        int pendantX = (CONTENT_MARGIN - AVATAR_IMAGE_WIDTH) / 2;
        int pendantY = AVATAR_PADDING;
        BufferedImage pendantImage = ImageUtil.getScaledImage(dto.getPendantImage(), AVATAR_IMAGE_WIDTH, AVATAR_IMAGE_WIDTH);
        g2d.drawImage(pendantImage, pendantX, pendantY, AVATAR_IMAGE_WIDTH, AVATAR_IMAGE_WIDTH, null);

        //endregion

        //region 绘制Up名称
        FontMetrics metrics_name = FontDesignMetrics.getMetrics(FONT_NAME);
        int text_name_height = metrics_name.getAscent();

        g2d.setFont(FONT_NAME);
        g2d.setColor(new Color(251, 114, 153));
        g2d.drawString(dto.getUname(), CONTENT_MARGIN, CONTENT_PADDING + text_name_height);
        //endregion

        //region 绘制动态推送时间
        FontMetrics metrics_date = FontDesignMetrics.getMetrics(FONT_DATE);
        int text_date_height = metrics_date.getAscent();

        g2d.setFont(FONT_DATE);
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.drawString(dto.getDateString(), CONTENT_MARGIN, CONTENT_PADDING  + TITLE_HEIGHT);
        //endregion

        //region 绘制正文
        int context_Y = CONTENT_PADDING  + TITLE_HEIGHT + CONTEXT_MARGIN;
        for (int i = 0; i < descArray.length; i++) {
            context_Y += drawParagraph(g2d, descArray[i], TEXT_1, Color.BLACK, maxWidth, context_Y) + TEXT_1_PARA_MARGIN;
        }
        //endregion

        //region 绘制图片
        int pan_image_x = CONTENT_MARGIN + (IMAGE_WIDTH - CONTENT_MARGIN * 2 - PICTURE_WIDTH) / 2; // 图片左上角x坐标
        int pan_image_y = context_Y;

        // 图片
        for (DynamicPictureBean bean : dto.getImageList()) {
            pan_image_y += PICTURE_MARGIN;
            int w = bean.getImageWidth();
            int h = bean.getImageHeight();
            int picHeight = PICTURE_WIDTH * h / w;
            g2d.drawImage(bean.getImage(), pan_image_x, pan_image_y, PICTURE_WIDTH, picHeight, null);
            pan_image_y += h;
        }
        //endregion

        //region 释放资源
        g2d.dispose();
        //endregion
        return target;
    }

    /**
     * 生成视频动态图片
     * @param dto
     */
    public static BufferedImage generateVideoDynamic(DynamicVideoDto dto) {
        //region 计算画板大小
        int height = CONTENT_PADDING  + TITLE_HEIGHT + CONTEXT_MARGIN;

        // 计算正文高度
        int maxWidth = IMAGE_WIDTH - 2 * CONTENT_MARGIN; // 正文最大宽度
        String desc = dto.getDescription();
        String[] descArray = desc.split("\n");
        for (String text : descArray) {
            FontMetrics metrics = FontDesignMetrics.getMetrics(TEXT_1);
            int text_height = metrics.getAscent();
            if (text.length() == 0) {
                height += text_height + TEXT_1_PARA_MARGIN;
            } else {
                int text_width = metrics.charsWidth(text.toCharArray(), 0, text.length());
                int rowCount = text_width / maxWidth + (text_width % maxWidth > 0 ? 1 : 0); // 总行数
                height += rowCount * (text_height + TEXT_1_LINE_MARGIN) - TEXT_1_LINE_MARGIN + TEXT_1_PARA_MARGIN;
            }
        }

        // 计算图片高度
        BufferedImage pic = dto.getVideoPic();
        int picHeight = pic.getHeight() * VIDEO_IMAGE_WIDTH / pic.getWidth();

        height += VIDEO_MARGIN + picHeight + CONTENT_PADDING;
        //endregion

        //region 准备画板
        BufferedImage target = new BufferedImage(IMAGE_WIDTH, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = target.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // 抗锯齿
        g2d.setBackground(Color.WHITE);
        g2d.clearRect(0, 0, IMAGE_WIDTH, height);
        //endregion

        //region 绘制头像

        // 将头像设置成圆形
        BufferedImage avatarImage = new BufferedImage(FACE_IMAGE_WIDTH, FACE_IMAGE_WIDTH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D avatarImageG2d = avatarImage.createGraphics();
        Ellipse2D.Double shape = new Ellipse2D.Double(0, 0, FACE_IMAGE_WIDTH, FACE_IMAGE_WIDTH);
        avatarImageG2d.setClip(shape);
        BufferedImage faceImage = ImageUtil.getScaledImage(dto.getFaceImage(), FACE_IMAGE_WIDTH, FACE_IMAGE_WIDTH);
        avatarImageG2d.drawImage(faceImage, 0, 0, FACE_IMAGE_WIDTH, FACE_IMAGE_WIDTH, null);
        avatarImageG2d.dispose();

        // 绘制头像
        int avatarX = (CONTENT_MARGIN - FACE_IMAGE_WIDTH) / 2;
        int avatarY = AVATAR_PADDING + (AVATAR_IMAGE_WIDTH - FACE_IMAGE_WIDTH) / 2;
        g2d.drawImage(avatarImage, avatarX, avatarY, FACE_IMAGE_WIDTH, FACE_IMAGE_WIDTH, null);

        // 绘制头像框
//        BufferedImage pendantImage = ImageUtil.getScaledImage(dto.getPendantImage(), AVATAR_IMAGE_WIDTH, AVATAR_IMAGE_WIDTH);
        int pendantX = (CONTENT_MARGIN - AVATAR_IMAGE_WIDTH) / 2;
        int pendantY = AVATAR_PADDING;
        BufferedImage pendantImage = ImageUtil.getScaledImage(dto.getPendantImage(), AVATAR_IMAGE_WIDTH, AVATAR_IMAGE_WIDTH);
        g2d.drawImage(pendantImage, pendantX, pendantY, AVATAR_IMAGE_WIDTH, AVATAR_IMAGE_WIDTH, null);

        //endregion

        //region 绘制Up名称
        FontMetrics metrics_name = FontDesignMetrics.getMetrics(FONT_NAME);
        int text_name_height = metrics_name.getAscent();

        g2d.setFont(FONT_NAME);
        g2d.setColor(new Color(251, 114, 153));
        g2d.drawString(dto.getUname(), CONTENT_MARGIN, CONTENT_PADDING + text_name_height);
        //endregion

        //region 绘制动态推送时间
        FontMetrics metrics_date = FontDesignMetrics.getMetrics(FONT_DATE);
        int text_date_height = metrics_date.getAscent();

        g2d.setFont(FONT_DATE);
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.drawString(dto.getDateString(), CONTENT_MARGIN, CONTENT_PADDING  + TITLE_HEIGHT);
        //endregion

        //region 绘制正文
        int context_Y = CONTENT_PADDING  + TITLE_HEIGHT + CONTEXT_MARGIN;
        for (int i = 0; i < descArray.length; i++) {
            context_Y += drawParagraph(g2d, descArray[i], TEXT_1, Color.BLACK, maxWidth, context_Y) + TEXT_1_PARA_MARGIN;
        }
        //endregion

        //region 绘制关联视频
        int pan_video_height = context_Y + VIDEO_MARGIN;
        int pan_video_content_x = CONTENT_MARGIN + VIDEO_IMAGE_WIDTH + 15; // 视频简介区域x坐标

        // 图片
        g2d.drawImage(pic, CONTENT_MARGIN, pan_video_height, VIDEO_IMAGE_WIDTH, picHeight,  null);

        // 标题
        FontMetrics metrics_title = FontDesignMetrics.getMetrics(TEXT_2);
        int text_2_height = metrics_title.getAscent();

        g2d.setFont(TEXT_2);
        g2d.setColor(Color.BLACK);
        g2d.drawString(dto.getVideoTitle(), pan_video_content_x, pan_video_height + 15 + text_2_height);

        // 数据统计
        pan_video_height += picHeight - 3 * (15 + text_2_height);
        String viewInfo = "播放量：" + dto.getVideoViewNum();
        g2d.drawString(viewInfo, pan_video_content_x, pan_video_height);
        pan_video_height += 15 + text_2_height;
        String likeInfo = "点赞量：" + dto.getVideoLikeNum();
        g2d.drawString(likeInfo, pan_video_content_x, pan_video_height);
        pan_video_height += 15 + text_2_height;
        String coinInfo = "投币量：" + dto.getVideoCoinNum();
        g2d.drawString(coinInfo, pan_video_content_x, pan_video_height);
        pan_video_height += 15 + text_2_height;
        String favoriteInfo = "收藏量：" + dto.getVideoFavoriteNum();
        g2d.drawString(favoriteInfo, pan_video_content_x, pan_video_height);
        //endregion

        //region 释放资源
        g2d.dispose();
        //endregion

        //TEST: 保存文件
        try {
            // 写入临时图片文件
            String tempPath = BotUtil.getGenshinFolderPath() + "/ttt" + dto.getUid() + ".png";
            ImageIO.write(target, "PNG", new File(tempPath));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return target;
    }

    /**
     * 绘画一段文字，并返回这段文字的高度
     * @param g2d
     * @param text
     * @param font
     * @param color
     * @param areaWidth
     * @param offsetY
     * @return
     */
    private static int drawParagraph(Graphics2D g2d, String text, Font font, Color color, int areaWidth, int offsetY) {
        FontMetrics metrics = FontDesignMetrics.getMetrics(font);
        int text_height = metrics.getAscent();

        if (text.length() == 0) {
            // 换行符不绘制
            return text_height;
        } else {
            int text_width = metrics.charsWidth(text.toCharArray(), 0, text.length());
            int contentHeight = offsetY;

            g2d.setFont(font);
            g2d.setColor(color);

            int rowPerChar = areaWidth * text.length() / text_width; // 每行最多字符数
            int rowCount = text_width / areaWidth + (text_width % areaWidth > 0 ? 1 : 0); // 总行数
            String temp;
            for (int i = 0; i < rowCount; i++) {
                if (i + 1 == rowCount) {
                    temp = text.substring(i * rowPerChar);
                } else {
                    temp = text.substring(i * rowPerChar, (i + 1) * rowPerChar);
                }
                contentHeight += text_height + TEXT_1_LINE_MARGIN;
                g2d.drawString(temp, CONTENT_MARGIN, contentHeight);
            }

            return contentHeight - TEXT_1_LINE_MARGIN - offsetY;
        }
    }
}
