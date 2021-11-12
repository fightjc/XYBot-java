package org.fightjc.xybot.util.genshin;

import org.apache.http.util.TextUtils;
import org.fightjc.xybot.pojo.genshin.CostDto;
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
        int EFFECT_ASCEND_MARGIN = 40; // 简介与特效标题间距
        int ASCEND_LINE_GAP = 20; // 素材间距
        int MATERIAL_ICON_WIDTH = 40; // 材料图标宽度

        Font f_name = new Font(Font.SANS_SERIF, Font.BOLD, 42); // 名称字体
        Font f_type = new Font(Font.SANS_SERIF, Font.BOLD, 21); // 类型字体
        Font f_des = new Font("Microsoft YaHei", Font.PLAIN, 21); // 描述字体
        Font f_effect_title = new Font(Font.SANS_SERIF, Font.BOLD, 28); // 特效标题字体
        Font f_effect = new Font(Font.MONOSPACED, Font.PLAIN, 21); // 特效内容 字体
        //endregion

        int currX = 0;
        int currY = 0;

        WeaponBean bean = dto.getInfo();

        //region 计算画板大小
        int width = 1000;
        int height = 0;
        // 名称
        {
            FontMetrics metrics_name = FontDesignMetrics.getMetrics(f_name);
            int text_name_height = metrics_name.getAscent();
            height += NAME_MARGIN + text_name_height + NAME_RARITY_MARGIN;
        }
        // 星级
        height += RARITY_WIDTH + RARITY_DES_MARGIN;
        // 简介
        {
            int des_width = width - NAME_MARGIN - (ICON_MARGIN + ICON_WIDTH + ICON_MARGIN);
            height += ImageUtil.getParagraphHeight(
                    bean.getDescription(),
                    f_des,
                    Color.BLACK,
                    des_width,
                    DES_LINE_GAP,
                    NAME_MARGIN,
                    currY) + DES_ATK_MARGIN;
        }
        // 基础攻击
        {
            FontMetrics metrics_baseAtk = FontDesignMetrics.getMetrics(f_effect);
            int text_baseAtk_height = metrics_baseAtk.getAscent();
            height += text_baseAtk_height;
            if (!TextUtils.isEmpty(bean.getSubStat())) {
                height += EFFECT_LINE_GAP + text_baseAtk_height;
            }
            height += ATK_EFFECT_MARGIN;
        }
        // 特效
        {
            FontMetrics metrics_effect = FontDesignMetrics.getMetrics(f_effect_title);
            int text_effect_height = metrics_effect.getAscent();
            height += text_effect_height + EFFECT_TITLE_MARGIN;

            int effect_width = width - EFFECT_MARGIN * 2;
            height += ImageUtil.getParagraphHeight(
                    bean.getLongEffect(),
                    f_effect,
                    Color.BLACK,
                    effect_width,
                    EFFECT_LINE_GAP,
                    EFFECT_MARGIN,
                    currY) + EFFECT_ASCEND_MARGIN;
        }
        // 突破材料表
        {
            FontMetrics metrics_ascend_title = FontDesignMetrics.getMetrics(f_effect_title);
            int text_ascend_title_height = metrics_ascend_title.getAscent();
            height += text_ascend_title_height + ASCEND_LINE_GAP;

            height += 3 * (MATERIAL_ICON_WIDTH + ASCEND_LINE_GAP) - ASCEND_LINE_GAP + NAME_MARGIN;
        }
        //endregion

        //region 准备画板
        BufferedImage target = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = target.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // 抗锯齿
        g2d.setBackground(new Color(246, 242, 238)); // #f6f2ee
        g2d.clearRect(0, 0, width, height);
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

        //region 绘制名称、星级、武器类型、简介、基础攻击和技能特效
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
        currX = EFFECT_MARGIN;
        FontMetrics metrics_baseAtk = FontDesignMetrics.getMetrics(f_effect);
        int text_baseAtk_height = metrics_baseAtk.getAscent();
        g2d.setFont(f_effect);
        g2d.setColor(Color.BLACK);
        g2d.drawString("基础攻击力：" + bean.getBaseAtk(),
                currX,
                currY + text_baseAtk_height);
        currY += text_baseAtk_height;
        if (!TextUtils.isEmpty(bean.getSubStat())) {
            currY += EFFECT_LINE_GAP;
            g2d.drawString(bean.getSubStat() + "：" + bean.getSubValue() + "%",
                    currX,
                    currY + text_baseAtk_height);
            currY += text_baseAtk_height;
        }
        currY += ATK_EFFECT_MARGIN;

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

        //region 突破材料表
        // 标题
        currX = NAME_MARGIN;
        currY += effect_height + EFFECT_ASCEND_MARGIN;
        FontMetrics metrics_ascend_title = FontDesignMetrics.getMetrics(f_effect_title);
        int text_ascend_title_height = metrics_ascend_title.getAscent();
        g2d.setFont(f_effect_title);
        g2d.setColor(Color.BLACK);
        g2d.drawString("突破材料表（满突破）",
                currX,
                currY + text_ascend_title_height);

        currY += text_ascend_title_height + ASCEND_LINE_GAP;

        FontMetrics metrics_ascend = FontDesignMetrics.getMetrics(f_effect);
        int text_ascend_height = metrics_ascend.getAscent();
        g2d.setFont(f_effect);
        g2d.setColor(Color.BLACK);

        // 摩拉
        BufferedImage moraImage = dto.getMora().getImage();
        if (moraImage != null) {
            g2d.drawImage(moraImage,
                    currX,
                    currY,
                    MATERIAL_ICON_WIDTH,
                    MATERIAL_ICON_WIDTH, null);
        }
        currX += MATERIAL_ICON_WIDTH;
        g2d.drawString(" x " + dto.getMora().getCount(),
                currX,
                currY + text_ascend_height);

        // 武器突破素材
        currX = NAME_MARGIN;
        currY += MATERIAL_ICON_WIDTH + ASCEND_LINE_GAP;
        for (CostDto cost : dto.getWeaponAscend()) {
            // 图片
            BufferedImage image = cost.getImage();
            if (image != null) {
                g2d.drawImage(image,
                        currX,
                        currY,
                        MATERIAL_ICON_WIDTH,
                        MATERIAL_ICON_WIDTH, null);
            }
            currX += MATERIAL_ICON_WIDTH;
            // 文字
            String text = " x " + cost.getCount();
            g2d.drawString(text,
                    currX,
                    currY + text_ascend_height);
            currX += metrics_ascend.charsWidth(text.toCharArray(), 0, text.length()) + ASCEND_LINE_GAP;
        }
        // 刷取日期
        if (dto.getWeaponAscend().size() > 0) {
            g2d.drawString(dto.getWeaponAscend().get(0).getInfo(),
                    currX,
                    currY + text_ascend_height);
        }

        // 角色突破素材
        currX = NAME_MARGIN;
        currY += MATERIAL_ICON_WIDTH + ASCEND_LINE_GAP;
        for (CostDto cost : dto.getAvatarAscend()) {
            // 图片
            BufferedImage image = cost.getImage();
            if (image != null) {
                g2d.drawImage(image,
                        currX,
                        currY,
                        MATERIAL_ICON_WIDTH,
                        MATERIAL_ICON_WIDTH, null);
            }
            currX += MATERIAL_ICON_WIDTH;
            // 文字
            String text = " x " + cost.getCount();
            g2d.drawString(text,
                    currX,
                    currY + text_ascend_height);
            currX += metrics_ascend.charsWidth(text.toCharArray(), 0, text.length()) + ASCEND_LINE_GAP;
        }
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
