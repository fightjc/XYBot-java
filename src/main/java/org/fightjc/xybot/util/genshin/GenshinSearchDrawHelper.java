package org.fightjc.xybot.util.genshin;

import org.fightjc.xybot.pojo.genshin.WeaponBean;
import org.fightjc.xybot.pojo.genshin.WeaponDrawDto;
import org.fightjc.xybot.util.BotUtil;
import org.fightjc.xybot.util.ImageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.font.FontDesignMetrics;

import java.awt.*;
import java.awt.image.BufferedImage;

public class GenshinSearchDrawHelper {

    private static final Logger logger = LoggerFactory.getLogger(GenshinSearchDrawHelper.class);

    public static BufferedImage drawCharacterInfo() {
        // TODO:
        return new BufferedImage(0, 0, BufferedImage.TYPE_INT_ARGB);
    }

    public static BufferedImage drawWeaponInfo(WeaponDrawDto dto) {
        //region 预设参数
        int ICON_WIDTH = 160; // 图标宽度
        int ICON_MARGIN = 30; // 图标与整体图片间距
        int NAME_MARGIN = 30; // 名称与整体图片间距
        int RARITY_WIDTH = 30; // 星级图片宽度
        int NAME_RARITY_MARGIN = 20; // 名称与星级间距
        int RARITY_MARGIN = 3; // 星级数间距
        int RARITY_TYPE_MARGIN = 20; // 星级与类型间距
        int RARITY_DES_MARGIN = 10; // 星级与简介间距
        int DES_LINE_GAP = 10; // 简介行间距
        int DES_ATK_MARGIN = 40; // 简介与基础属性标题间距
        int ATK_EFFECT_MARGIN = 40; // 简介与特效标题间距
        int EFFECT_TITLE_MARGIN = 15; // 特效标题与内容间距
        int EFFECT_MARGIN = 30; // 特效内容与整体图片间距
        int EFFECT_LINE_GAP = 10; // 特效内容行间距

        Font f_name = new Font(Font.SANS_SERIF, Font.BOLD, 42); // 名称字体
        Font f_type = new Font(Font.SANS_SERIF, Font.BOLD, 21); // 类型字体
        Font f_des = new Font("Microsoft YaHei", Font.PLAIN, 21); // 描述字体
        Font f_effect_title = new Font(Font.SANS_SERIF, Font.BOLD, 28); // 特效标题字体
        Font f_effect = new Font("Microsoft YaHei", Font.PLAIN, 21); // 特效标题字体
        //endregion

        //region 计算画板大小
        int width = 1000;
        int height = 600;
        //endregion

        //region 准备画板
        BufferedImage target = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = target.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // 抗锯齿
        g2d.setBackground(new Color(246, 242, 238)); // #f6f2ee
        g2d.clearRect(0, 0, 1000, 600);
        //endregion

        //region 绘制武器图标
        BufferedImage icon = dto.getAwakenIcon();
        if (icon != null) {
            g2d.drawImage(icon,
                    width - ICON_WIDTH - ICON_MARGIN,
                    ICON_MARGIN,
                    ICON_WIDTH,
                    ICON_WIDTH, null);
        }
        //endregion

        //region 绘制名称、星级、武器类型、简介和技能特效
        WeaponBean bean = dto.getInfo();

        int currX = 0;
        int currY = 0;

        // 名称
        FontMetrics metrics_name = FontDesignMetrics.getMetrics(f_name);
        int text_name_height = metrics_name.getAscent();
        g2d.setFont(f_name);
        g2d.setColor(Color.BLACK);
        g2d.drawString(bean.getName(),
                NAME_MARGIN,
                currY + NAME_MARGIN + text_name_height);

        currY += NAME_MARGIN + text_name_height + NAME_RARITY_MARGIN;
        currX += NAME_MARGIN;

        // 星级
        String starFilePath = BotUtil.getGenshinFolderPath() + "/images/miscellaneous/star.png";
        BufferedImage starImage = BotUtil.readImageFile(starFilePath);
        if (starImage == null) {
            logger.error("获取 /images/miscellaneous/star.png 对象失败");
        } else {
            try {
                int rarity = Integer.parseInt(bean.getRarity());
                for (int i = 0; i < rarity; i++) {
                    g2d.drawImage(starImage,
                            currX,
                            currY,
                            RARITY_WIDTH,
                            RARITY_WIDTH, null);
                    currX += RARITY_MARGIN + RARITY_WIDTH;
                }
            } catch (NumberFormatException e) {
                logger.error("尝试转换星级错误：" + bean.getRarity());
            }
        }

        // 武器类型
        FontMetrics metrics_type = FontDesignMetrics.getMetrics(f_type);
        int text_type_height = metrics_type.getAscent();
        g2d.setFont(f_type);
        g2d.setColor(Color.BLACK);
        g2d.drawString(bean.getWeaponType(),
                currX + RARITY_TYPE_MARGIN,
                currY + text_type_height);

        currX = NAME_MARGIN;
        currY += RARITY_WIDTH + RARITY_DES_MARGIN;

        // 简介
        int des_width = width - NAME_MARGIN - (ICON_MARGIN + ICON_WIDTH + ICON_MARGIN);
        int des_height = ImageUtil.drawParagraph(g2d,
                bean.getDescription(),
                f_des,
                Color.GRAY,
                des_width,
                DES_LINE_GAP,
                currX,
                currY);

        currY += des_height + DES_ATK_MARGIN;

        // 基础攻击和副属性
        // TODO: 显示满级基础攻击和副属性
        int baseAtk_width = width - EFFECT_MARGIN * 2;
        String baseAtkInfo = "基础攻击力：" + bean.getBaseAtk();
        String subAtkInfo = bean.getSubStat() + "：" + bean.getSubValue() + " %";
        int baseAtk_height = ImageUtil.drawParagraph(g2d,
                baseAtkInfo,
                f_effect,
                Color.BLACK,
                baseAtk_width,
                EFFECT_LINE_GAP,
                EFFECT_MARGIN,
                currY);
        currY += baseAtk_height + EFFECT_LINE_GAP;
        int subAtk_height = ImageUtil.drawParagraph(g2d,
                subAtkInfo,
                f_effect,
                Color.BLACK,
                baseAtk_width,
                EFFECT_LINE_GAP,
                EFFECT_MARGIN,
                currY);

        currY += subAtk_height + ATK_EFFECT_MARGIN;

        // 技能特效标题
        FontMetrics metrics_effect = FontDesignMetrics.getMetrics(f_effect_title);
        int text_effect_height = metrics_effect.getAscent();
        g2d.setFont(f_effect_title);
        g2d.setColor(Color.BLACK);
        g2d.drawString(bean.getEffectName(),
                currX,
                currY + text_effect_height);

        currY += text_effect_height + EFFECT_TITLE_MARGIN;

        // 技能特效内容
        int effect_width = width - EFFECT_MARGIN * 2;
        int effect_height = ImageUtil.drawParagraph(g2d,
                bean.getLongEffect(),
                f_effect,
                Color.BLACK,
                effect_width,
                EFFECT_LINE_GAP,
                EFFECT_MARGIN,
                currY);
        //endregion

        currY += effect_height;

        //region 突破材料表

        //endregion

        //region 释放资源
        g2d.dispose();
        //endregion

        return target;
    }

    public static BufferedImage drawMaterialInfo() {
        // TODO:
        return new BufferedImage(0, 0, BufferedImage.TYPE_INT_ARGB);
    }
}
