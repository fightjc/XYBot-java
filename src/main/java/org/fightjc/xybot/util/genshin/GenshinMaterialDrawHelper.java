package org.fightjc.xybot.util.genshin;

import com.idrsolutions.image.png.PngCompressor;
import org.fightjc.xybot.pojo.ResultOutput;
import org.fightjc.xybot.pojo.genshin.MaterialResultDto;
import org.fightjc.xybot.pojo.genshin.NameMapBean;
import org.fightjc.xybot.util.BotUtil;
import org.fightjc.xybot.util.ImageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.font.FontDesignMetrics;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class GenshinMaterialDrawHelper {

    private static final Logger logger = LoggerFactory.getLogger(GenshinMaterialDrawHelper.class);

    private final static int IMAGE_MARGIN = 50;

    private final static int START_WIDTH = 28;          // 角落星形图案宽度
    private final static int START_HEIGHT = 28;         // 角落星形图案高度
    private final static int BORDER_HEADER_WIDTH = 110; // 头部边框图案宽度
    private final static int BORDER_HEADER_HEIGHT = 38; // 头部边框高度
    private final static int BORDER_FOOTER_WIDTH = 66;  // 底部边框图案宽度
    private final static int BORDER_FOOTER_HEIGHT = 20; // 底部边框高度

    private final static int HEADER_1_HEIGHT = 50;     // 标题1字体高度
    private final static int HEADER_1_MARGIN = 20;     // 标题1边缘
    private final static int HEADER_2_HEIGHT = 35;     // 标题2字体高度
    private final static int HEADER_2_MARGIN = 20;     // 标题2边缘
    private final static int MATERIAL_WIDTH = 64;      // 材料图片宽度
    private final static int MATERIAL_HEIGHT = 64;     // 材料图片高度
    private final static int MATERIAL_MARGIN = 20;     // 材料图片边缘
    private final static int CHAR_WEAPON_WIDTH = 106;  // 角色或武器图片宽度
    private final static int CHAR_WEAPON_HEIGHT = 106; // 角色或武器图片高度
    private final static int DISPLAY_WIDTH = 112;      // 整合图片宽度
    private final static int DISPLAY_HEIGHT = 136;     // 整合图片高度
    private final static int DISPLAY_MARGIN = 24;      // 整合图片边缘
    private final static int countPerLine = 8;         // 一行显示多少匹配角色或武器

    private final static Font HEADER_1 = new Font(Font.MONOSPACED, Font.BOLD, 42);        // 大标题
    private final static Font HEADER_2 = new Font(Font.SANS_SERIF, Font.BOLD, 32);        // 小标题
    private final static Font TEXT_1 = new Font("Microsoft YaHei", Font.PLAIN, 18); // 解释文字

    // 文本对齐方向
    enum TEXT_ALIGNMENT {
        LEFT, CENTER, RIGHT;
    }

    /**
     * 绘制每日素材图片并保存到文件
     * @param data
     * @return
     */
    public static ResultOutput drawDailyMaterial(List<MaterialResultDto> data) {
        for (MaterialResultDto dto : data) {
            List<MaterialResultDto.MaterialInfo> talentMaterialList = dto.talentMaterialList;
            List<MaterialResultDto.MaterialInfo> weaponMaterialList = dto.weaponMaterialList;

            // region 预计算画板大小

            int width = countPerLine * (DISPLAY_WIDTH + DISPLAY_MARGIN) - DISPLAY_MARGIN + IMAGE_MARGIN * 2;
            int height = IMAGE_MARGIN * 2;
            if (talentMaterialList.size() > 0) {
                height += HEADER_1_MARGIN + HEADER_1_HEIGHT + HEADER_1_MARGIN + BORDER_HEADER_HEIGHT;
                for (MaterialResultDto.MaterialInfo info : talentMaterialList) {
                    int line = (info.star5Result.size() + info.star4Result.size() - 1) / countPerLine + 1; // 计算匹配行数
                    height += MATERIAL_HEIGHT + MATERIAL_MARGIN * 2 + line * (DISPLAY_HEIGHT + DISPLAY_MARGIN);
                }
                height += BORDER_FOOTER_HEIGHT;
            }
            if (talentMaterialList.size() > 0) {
                height += HEADER_1_MARGIN + HEADER_1_HEIGHT + HEADER_1_MARGIN + BORDER_HEADER_HEIGHT;
                for (MaterialResultDto.MaterialInfo info : weaponMaterialList) {
                    int line = (info.star5Result.size() + info.star4Result.size() - 1) / countPerLine + 1; // 计算匹配行数
                    height += MATERIAL_HEIGHT + MATERIAL_MARGIN * 2 + line * (DISPLAY_HEIGHT + DISPLAY_MARGIN);
                }
                height += BORDER_FOOTER_HEIGHT;
            }

            // endregion

            // 准备画板
            BufferedImage target = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = target.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // 抗锯齿
            g2d.setBackground(new Color(246, 242, 238)); // #f6f2ee
            g2d.clearRect(0, 0, width, height);

            int currentOffsetY = IMAGE_MARGIN; // 记录下一次绘图的纵坐标

            // region 绘制天赋材料

            if (talentMaterialList.size() > 0) {
                currentOffsetY += HEADER_1_MARGIN;
                // 标题
                drawTitleStringWithBorder(g2d, width, dto.day + "素材可升天赋角色", HEADER_1, Color.black,
                        0, currentOffsetY, TEXT_ALIGNMENT.CENTER);

                currentOffsetY += HEADER_1_HEIGHT + HEADER_1_MARGIN;

                // 修饰边框
                drawHeaderBorderImage(g2d, width, currentOffsetY);
                currentOffsetY += BORDER_HEADER_HEIGHT;

                // 角色天赋材料
                for (int j = 0; j < talentMaterialList.size(); j++) {
                    MaterialResultDto.MaterialInfo info = talentMaterialList.get(j);
                    currentOffsetY += MATERIAL_MARGIN;

                    // 国家
                    int[] textSize_region = drawString(g2d, width, info.region, HEADER_2, Color.black,
                            IMAGE_MARGIN, currentOffsetY, TEXT_ALIGNMENT.LEFT);

                    // 刷取地点
                    drawString(g2d, width, info.location, HEADER_2, Color.black,
                            IMAGE_MARGIN + textSize_region[0] + HEADER_2_MARGIN, currentOffsetY, TEXT_ALIGNMENT.LEFT);

                    // 天赋材料名称
                    int offsetX_name = 3 * MATERIAL_WIDTH + 4 * MATERIAL_MARGIN + IMAGE_MARGIN; // 在三张天赋材料图片的左边
                    drawString(g2d, width, info.name, HEADER_2, Color.black,
                            offsetX_name, currentOffsetY, TEXT_ALIGNMENT.RIGHT);

                    // 天赋材料列表
                    int offsetX_material = width - offsetX_name + MATERIAL_MARGIN;
                    drawMaterialImage(g2d, info.star4, "4", offsetX_material, currentOffsetY);
                    offsetX_material += MATERIAL_WIDTH + MATERIAL_MARGIN;
                    drawMaterialImage(g2d, info.star3, "3", offsetX_material, currentOffsetY);
                    offsetX_material += MATERIAL_WIDTH + MATERIAL_MARGIN;
                    drawMaterialImage(g2d, info.star2, "2", offsetX_material, currentOffsetY);

                    currentOffsetY += MATERIAL_HEIGHT + MATERIAL_MARGIN;

                    int count = 0; // 当前匹配角色数，控制是否换行
                    int offsetX_character = IMAGE_MARGIN;

                    // 角色五星列表
                    for (int k = 0; k < info.star5Result.size(); k++) {
                        drawCharacterImage(g2d, info.star5Result.get(k), "5", offsetX_character, currentOffsetY);
                        if (++count % countPerLine == 0) {
                            // 换行
                            offsetX_character = IMAGE_MARGIN;
                            currentOffsetY += DISPLAY_HEIGHT + DISPLAY_MARGIN;
                        } else {
                            offsetX_character += DISPLAY_WIDTH + DISPLAY_MARGIN;
                        }
                    }

                    // 角色四星列表
                    for (int k = 0; k < info.star4Result.size(); k++) {
                        drawCharacterImage(g2d, info.star4Result.get(k), "4", offsetX_character, currentOffsetY);
                        if (++count % countPerLine == 0) {
                            // 换行
                            offsetX_character = IMAGE_MARGIN;
                            currentOffsetY += DISPLAY_HEIGHT + DISPLAY_MARGIN;
                        } else {
                            offsetX_character += DISPLAY_WIDTH + DISPLAY_MARGIN;
                        }
                    }

                    // 如果当前没有换行则执行一次换行操作
                    if (count % countPerLine != 0) {
                        currentOffsetY += DISPLAY_HEIGHT + DISPLAY_MARGIN;
                    }
                }

                // 修饰边框
                drawFooterBorderImage(g2d, width, currentOffsetY);
                currentOffsetY += BORDER_FOOTER_HEIGHT;
            }

            // endregion

            // region 绘制武器材料

            if (weaponMaterialList.size() > 0) {
                currentOffsetY += HEADER_1_MARGIN;
                // 标题
                drawTitleStringWithBorder(g2d, width, dto.day + "素材可突破武器", HEADER_1, Color.black,
                        0, currentOffsetY, TEXT_ALIGNMENT.CENTER);
                currentOffsetY += HEADER_1_HEIGHT + HEADER_1_MARGIN;

                // 修饰边框
                drawHeaderBorderImage(g2d, width, currentOffsetY);
                currentOffsetY += BORDER_HEADER_HEIGHT;

                // 武器升级材料
                for (int j = 0; j < weaponMaterialList.size(); j++) {
                    MaterialResultDto.MaterialInfo info = weaponMaterialList.get(j);
                    currentOffsetY += MATERIAL_MARGIN;

                    // 国家
                    int[] textSize_region = drawString(g2d, width, info.region, HEADER_2, Color.black,
                            IMAGE_MARGIN, currentOffsetY, TEXT_ALIGNMENT.LEFT);

                    // 刷取地点
                    drawString(g2d, width, info.location, HEADER_2, Color.black,
                            IMAGE_MARGIN + textSize_region[0] + HEADER_2_MARGIN, currentOffsetY, TEXT_ALIGNMENT.LEFT);

                    // 武器材料名称
                    int offsetX_name = 4 * MATERIAL_WIDTH + 5 * MATERIAL_MARGIN + IMAGE_MARGIN; // 在四张武器材料图片的左边
                    drawString(g2d, width, info.name, HEADER_2, Color.black,
                            offsetX_name, currentOffsetY, TEXT_ALIGNMENT.RIGHT);

                    // 武器材料列表
                    int offsetX_material = width - offsetX_name + MATERIAL_MARGIN;
                    drawMaterialImage(g2d, info.star5, "5", offsetX_material, currentOffsetY);
                    offsetX_material += MATERIAL_WIDTH + MATERIAL_MARGIN;
                    drawMaterialImage(g2d, info.star4, "4", offsetX_material, currentOffsetY);
                    offsetX_material += MATERIAL_WIDTH + MATERIAL_MARGIN;
                    drawMaterialImage(g2d, info.star3, "3", offsetX_material, currentOffsetY);
                    offsetX_material += MATERIAL_WIDTH + MATERIAL_MARGIN;
                    drawMaterialImage(g2d, info.star2, "2", offsetX_material, currentOffsetY);

                    currentOffsetY += MATERIAL_HEIGHT + MATERIAL_MARGIN;

                    int count = 0; // 当前匹配武器数，控制是否换行
                    int offsetX_character = IMAGE_MARGIN;

                    // 武器五星列表
                    for (int k = 0; k < info.star5Result.size(); k++) {
                        drawWeaponImage(g2d, info.star5Result.get(k), "5", offsetX_character, currentOffsetY);
                        if (++count % countPerLine == 0) {
                            // 换行
                            offsetX_character = IMAGE_MARGIN;
                            currentOffsetY += DISPLAY_HEIGHT + DISPLAY_MARGIN;
                        } else {
                            offsetX_character += DISPLAY_WIDTH + DISPLAY_MARGIN;
                        }
                    }

                    // 武器四星列表
                    for (int k = 0; k < info.star4Result.size(); k++) {
                        drawWeaponImage(g2d, info.star4Result.get(k), "4", offsetX_character, currentOffsetY);
                        if (++count % countPerLine == 0) {
                            // 换行
                            offsetX_character = IMAGE_MARGIN;
                            currentOffsetY += DISPLAY_HEIGHT + DISPLAY_MARGIN;
                        } else {
                            offsetX_character += DISPLAY_WIDTH + DISPLAY_MARGIN;
                        }
                    }

                    // 如果当前没有换行则执行一次换行操作
                    if (count % countPerLine != 0) {
                        currentOffsetY += DISPLAY_HEIGHT + DISPLAY_MARGIN;
                    }
                }

                // 修饰边框
                drawFooterBorderImage(g2d, width, currentOffsetY);
                currentOffsetY += BORDER_FOOTER_HEIGHT;
            }

            // endregion

            // 绘制整体边框修饰
            drawImageBorder(g2d, width, height);

            g2d.dispose();

            // 保存文件
            try {
                // 写入临时图片文件
                String tempPath = BotUtil.getGenshinFolderPath() + "/dailymaterial_" + dto.getDayNum() + "_temp.png";
                ImageIO.write(target, "PNG", new File(tempPath));

                // 压缩png
                String pathName = BotUtil.getGenshinFolderPath() + "/dailymaterial_" + dto.getDayNum() + ".png";
                File tempFile = new File(tempPath);
                File dstFile = new File(pathName);
                PngCompressor.compress(tempFile, dstFile);

                // 删除临时图片文件
                tempFile.delete();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        return new ResultOutput<>(true, "生成每日素材图片成功");
    }

    /**
     * 绘制画板边框
     * @param g2d 画板
     * @param width 画板宽度
     * @param height 画板高度
     */
    public static void drawImageBorder(Graphics2D g2d, int width, int height) {
        String starFilePath = BotUtil.getGenshinFolderPath() + "/images/miscellaneous/star_corner.png";
        BufferedImage starImage = BotUtil.readImageFile(starFilePath);
        if (starImage == null) {
            logger.error("获取 /images/miscellaneous/star_corner.png 对象失败");
            return;
        }
        starImage = ImageUtil.getScaledImage(starImage, START_WIDTH, START_HEIGHT);

        int offsetX = (IMAGE_MARGIN - START_WIDTH) / 2;
        int offsetY = (IMAGE_MARGIN - START_HEIGHT) / 2;

        // 左上
        g2d.drawImage(starImage, offsetX, offsetY, START_WIDTH, START_HEIGHT, null);
        g2d.drawArc(0, 0, IMAGE_MARGIN, IMAGE_MARGIN, 270, 90);
        // 左下
        g2d.drawImage(starImage, offsetX, height - offsetY - START_HEIGHT, START_WIDTH, START_HEIGHT, null);
        g2d.drawArc(0, height - IMAGE_MARGIN, IMAGE_MARGIN, IMAGE_MARGIN, 0, 90);
        // 右上
        g2d.drawImage(starImage, width - offsetX - START_WIDTH, offsetY, START_WIDTH, START_HEIGHT, null);
        g2d.drawArc(width - IMAGE_MARGIN, 0, IMAGE_MARGIN, IMAGE_MARGIN, 180, 90);
        // 右下
        g2d.drawImage(starImage, width - offsetX - START_WIDTH, height - offsetY - START_HEIGHT, START_WIDTH, START_HEIGHT, null);
        g2d.drawArc(width - IMAGE_MARGIN, height - IMAGE_MARGIN, IMAGE_MARGIN, IMAGE_MARGIN, 90, 90);

        int arcRadius = IMAGE_MARGIN / 2; // 角落圆形半径
        // 左上至左下
        g2d.drawLine(arcRadius, IMAGE_MARGIN, arcRadius, height - IMAGE_MARGIN);
        // 右上至右下
        g2d.drawLine(width - arcRadius, IMAGE_MARGIN, width - arcRadius, height - IMAGE_MARGIN);
        // 左上至右上
        g2d.drawLine(IMAGE_MARGIN, arcRadius, width - IMAGE_MARGIN, arcRadius);
        // 左下至右下
        g2d.drawLine(IMAGE_MARGIN, height - arcRadius, width - IMAGE_MARGIN, height - arcRadius);
    }

    /**
     * 绘制头部边框
     * @param g2d 画板
     * @param width 画板宽度
     * @param y 绘制高度
     */
    private static void drawHeaderBorderImage(Graphics2D g2d, int width, int y) {
        // 获取头部边框图形
        String headerFilePath = BotUtil.getGenshinFolderPath() + "/images/miscellaneous/border_header.png";
        BufferedImage headerImage = BotUtil.readImageFile(headerFilePath);
        if (headerImage == null) {
            logger.error("获取 /images/miscellaneous/border_header.png 对象失败");
            return;
        }
        headerImage = ImageUtil.getScaledImage(headerImage, BORDER_HEADER_WIDTH, BORDER_HEADER_HEIGHT);

        // 获取头部边框线段图形
        int lineWidth = (width - BORDER_HEADER_WIDTH) / 2 - IMAGE_MARGIN;
        String lineFilePath = BotUtil.getGenshinFolderPath() + "/images/miscellaneous/border_line.png";
        BufferedImage lineImage = BotUtil.readImageFile(lineFilePath);
        if (lineImage == null) {
            logger.error("获取 /images/miscellaneous/border_line.png 对象失败");
            return;
        }
        lineImage = ImageUtil.getScaledImage(lineImage, lineWidth, BORDER_FOOTER_HEIGHT);

        // 左侧
        g2d.drawImage(lineImage, IMAGE_MARGIN, y + (BORDER_HEADER_HEIGHT - BORDER_FOOTER_HEIGHT), lineWidth, BORDER_FOOTER_HEIGHT, null);

        // 中间
        g2d.drawImage(headerImage, IMAGE_MARGIN + lineWidth, y, BORDER_HEADER_WIDTH, BORDER_HEADER_HEIGHT, null);

        // 左侧
        g2d.drawImage(lineImage, IMAGE_MARGIN + lineWidth + BORDER_HEADER_WIDTH, y + (BORDER_HEADER_HEIGHT - BORDER_FOOTER_HEIGHT), lineWidth, BORDER_FOOTER_HEIGHT, null);
    }

    /**
     * 绘制底部边框
     * @param g2d 画板
     * @param width 画板宽度
     * @param y 绘制高度
     */
    private static void drawFooterBorderImage(Graphics2D g2d, int width, int y) {
        // 获取底部边框左侧图形
        String leftFilePath = BotUtil.getGenshinFolderPath() + "/images/miscellaneous/border_corner_left.png";
        BufferedImage leftImage = BotUtil.readImageFile(leftFilePath);
        if (leftImage == null) {
            logger.error("获取 /images/miscellaneous/border_corner_left.png 对象失败");
            return;
        }
        leftImage = ImageUtil.getScaledImage(leftImage, BORDER_FOOTER_WIDTH, BORDER_FOOTER_HEIGHT);

        // 获取底部边框线段图形
        int lineWidth = width - 2 * BORDER_FOOTER_WIDTH - 2 * IMAGE_MARGIN;
        String lineFilePath = BotUtil.getGenshinFolderPath() + "/images/miscellaneous/border_line.png";
        BufferedImage lineImage = BotUtil.readImageFile(lineFilePath);
        if (lineImage == null) {
            logger.error("获取 /images/miscellaneous/border_line.png 对象失败");
            return;
        }
        lineImage = ImageUtil.getScaledImage(lineImage, lineWidth, BORDER_FOOTER_HEIGHT);

        // 获取底部边框右侧图形
        String rightFilePath = BotUtil.getGenshinFolderPath() + "/images/miscellaneous/border_corner_right.png";
        BufferedImage rightImage = BotUtil.readImageFile(rightFilePath);
        if (rightImage == null) {
            logger.error("获取 /images/miscellaneous/border_corner_right.png 对象失败");
            return;
        }
        rightImage = ImageUtil.getScaledImage(rightImage, BORDER_FOOTER_WIDTH, BORDER_FOOTER_HEIGHT);

        // 左侧
        g2d.drawImage(leftImage, IMAGE_MARGIN, y, BORDER_FOOTER_WIDTH, BORDER_FOOTER_HEIGHT, null);

        // 中间
        g2d.drawImage(lineImage, IMAGE_MARGIN + BORDER_FOOTER_WIDTH, y, lineWidth, BORDER_FOOTER_HEIGHT, null);

        // 右侧
        g2d.drawImage(rightImage, IMAGE_MARGIN + BORDER_FOOTER_WIDTH + lineWidth, y, BORDER_FOOTER_WIDTH, BORDER_FOOTER_HEIGHT, null);
    }

    /**
     * 绘制材料图片
     * @param g2d 主画板
     * @param bean 材料信息
     * @param rarity 材料星级
     * @param x 材料绘制在主画板的位置
     * @param y 材料绘制在主画板的位置
     */
    private static void drawMaterialImage(Graphics2D g2d, NameMapBean bean, String rarity, int x, int y) {
        // 获取指定大小材料前景图
        String name = bean.getNameMap().substring(0, bean.getNameMap().indexOf("."));
        String fgFilePath = BotUtil.getGenshinFolderPath() + "/images/materials/" + name + ".png";
        BufferedImage fgImage = BotUtil.readImageFile(fgFilePath);
        if (fgImage == null) {
            logger.error("获取 /images/materials/" + name + ".png 对象失败");
            return;
        }
        fgImage = ImageUtil.getScaledImage(fgImage, MATERIAL_WIDTH, MATERIAL_HEIGHT);

        // 获取指定大小材料背景图
        String bgFilePath = BotUtil.getGenshinFolderPath() + "/images/miscellaneous/bg_rarity_" + rarity + ".png";
        BufferedImage bgImage = BotUtil.readImageFile(bgFilePath);
        if (bgImage == null) {
            logger.error("获取 /images/miscellaneous/bg_rarity_" + rarity + ".png 对象失败");
            return;
        }
        bgImage = ImageUtil.getScaledImage(bgImage, MATERIAL_WIDTH, MATERIAL_HEIGHT);

        Graphics2D bgImageG2d = bgImage.createGraphics();

        // 绘制材料前景图到材料背景图
        bgImageG2d.drawImage(fgImage, 0, 0,
                MATERIAL_WIDTH, MATERIAL_HEIGHT, null);

        bgImageG2d.dispose();

        // 绘制材料合成图到主画板
        g2d.drawImage(bgImage, x, y, MATERIAL_WIDTH, MATERIAL_HEIGHT, null);
    }

    /**
     * 绘制角色图片
     * @param g2d 主画板
     * @param bean 材料信息
     * @param rarity 材料星级
     * @param x 材料绘制在主画板的位置
     * @param y 材料绘制在主画板的位置
     */
    private static void drawCharacterImage(Graphics2D g2d, NameMapBean bean, String rarity, int x, int y) {
        // 获取指定大小角色前景图
        String name = bean.getNameMap().substring(0, bean.getNameMap().indexOf("."));
        String fgFilePath = BotUtil.getGenshinFolderPath() + "/images/characters/" + name + ".png";
        BufferedImage fgImage = BotUtil.readImageFile(fgFilePath);
        if (fgImage == null) {
            logger.error("获取 /images/characters/" + name + ".png 对象失败");
            return;
        }
        fgImage = ImageUtil.getScaledImage(fgImage, CHAR_WEAPON_WIDTH, CHAR_WEAPON_HEIGHT);

        // 获取指定大小角色背景图
        String bgFilePath = BotUtil.getGenshinFolderPath() + "/images/miscellaneous/bg_rarity_" + rarity + "_with_text.png";
        BufferedImage bgImage = BotUtil.readImageFile(bgFilePath);
        if (bgImage == null) {
            logger.error("获取 /images/miscellaneous/bg_rarity_" + rarity + "_with_text.png 对象失败");
            return;
        }
        bgImage = ImageUtil.getScaledImage(bgImage, DISPLAY_WIDTH, DISPLAY_HEIGHT);

        Graphics2D bgImageG2d = bgImage.createGraphics();

        // 绘制角色前景图到角色背景图
        bgImageG2d.drawImage(fgImage, (DISPLAY_WIDTH - CHAR_WEAPON_WIDTH) / 2, (DISPLAY_WIDTH - CHAR_WEAPON_HEIGHT) / 2,
                CHAR_WEAPON_WIDTH, CHAR_WEAPON_HEIGHT, null);

        // 绘制角色名字到角色背景图
        drawString(bgImageG2d, DISPLAY_WIDTH, bean.getName(), TEXT_1, Color.BLACK,
                0, CHAR_WEAPON_HEIGHT + 4, TEXT_ALIGNMENT.CENTER);

        bgImageG2d.dispose();

        // 绘制角色合成图到主画板
        g2d.drawImage(bgImage, x, y, DISPLAY_WIDTH, DISPLAY_HEIGHT, null);
    }

    /**
     * 绘制武器图片
     * @param g2d 主画板
     * @param bean 材料信息
     * @param rarity 材料星级
     * @param x 材料绘制在主画板的位置
     * @param y 材料绘制在主画板的位置
     */
    private static void drawWeaponImage(Graphics2D g2d, NameMapBean bean, String rarity, int x, int y) {
        // 获取指定大小武器前景图
        String name = bean.getNameMap().substring(0, bean.getNameMap().indexOf("."));
        String fgFilePath = BotUtil.getGenshinFolderPath() + "/images/weapons/" + name + ".png";
        BufferedImage fgImage = BotUtil.readImageFile(fgFilePath);
        if (fgImage == null) {
            logger.error("获取 /images/weapons/" + name + ".png 对象失败");
            return;
        }
        fgImage = ImageUtil.getScaledImage(fgImage, CHAR_WEAPON_WIDTH, CHAR_WEAPON_HEIGHT);

        // 获取指定大小武器背景图
        String bgFilePath = BotUtil.getGenshinFolderPath() + "/images/miscellaneous/bg_rarity_" + rarity + "_with_text.png";
        BufferedImage bgImage = BotUtil.readImageFile(bgFilePath);
        if (bgImage == null) {
            logger.error("获取 /images/miscellaneous/bg_rarity_" + rarity + "_with_text.png 对象失败");
            return;
        }
        bgImage = ImageUtil.getScaledImage(bgImage, DISPLAY_WIDTH, DISPLAY_HEIGHT);

        Graphics2D bgImageG2d = bgImage.createGraphics();

        // 绘制武器前景图到武器背景图
        bgImageG2d.drawImage(fgImage, (DISPLAY_WIDTH - CHAR_WEAPON_WIDTH) / 2, (DISPLAY_WIDTH - CHAR_WEAPON_HEIGHT) / 2,
                CHAR_WEAPON_WIDTH, CHAR_WEAPON_HEIGHT, null);

        // 绘制武器名字到武器背景图
        drawString(bgImageG2d, DISPLAY_WIDTH, bean.getName(), TEXT_1, Color.BLACK,
                0, CHAR_WEAPON_HEIGHT + 4, TEXT_ALIGNMENT.CENTER);

        bgImageG2d.dispose();

        // 绘制武器合成图到主画板
        g2d.drawImage(bgImage, x, y, DISPLAY_WIDTH, DISPLAY_HEIGHT, null);
    }

    /**
     * 绘制带边框文本，当前暂时只用于标题
     * @param g2d 画板
     * @param width 画板宽度
     * @param text 文字文本
     * @param font 字体样式
     * @param color 字体颜色
     * @param offsetX 文字左上角在画板中的x位置，如果选择对齐方式为靠右，则文字右上角
     * @param offsetY 文字左上角在画板中的y位置，如果选择对齐方式为靠右，则文字右上角
     * @param alignment 文字对齐方式
     * @return 绘制文本的宽度和高度
     */
    private static void drawTitleStringWithBorder(Graphics2D g2d, int width, String text,
                                              Font font, Color color, int offsetX, int offsetY, TEXT_ALIGNMENT alignment) {
        // x : border | padding | padding | text | padding | padding | border

        int padding = 15; // 基础内边距
        int newOffsetX = offsetX; // 带边框的新x偏移量
        switch (alignment) {
            case LEFT:
                newOffsetX += padding;
                break;
            case CENTER:
//                newOffsetX = offsetX;
                break;
            case RIGHT:
                newOffsetX -= padding;
                break;
        }

        // 绘制文本
        int[] size = drawString(g2d, width, text, font, color, newOffsetX, offsetY, alignment);

        // 获取指定大小的文本边框图
        String outerFilePath = BotUtil.getGenshinFolderPath() + "/images/miscellaneous/text_out.png";
        BufferedImage outerImage = BotUtil.readImageFile(outerFilePath);
        if (outerImage == null) {
            logger.error("获取 /images/miscellaneous/text_out.png 对象失败");
            return;
        }
        outerImage = ImageUtil.getScaledImage(outerImage, size[0] + 4 * padding, size[1] + 3 * padding);

        // 计算边框图片的位置
        int x = 0;
        int y = offsetY - padding;
        switch (alignment) {
            case LEFT:
                x = offsetX;
                break;
            case CENTER:
                x = (width - size[0]) / 2 + offsetX - 2 * padding;
                break;
            case RIGHT:
                x = width - size[0] - offsetX - 4 * padding;
                break;
        }

        // 绘制文本边框
        g2d.drawImage(outerImage, x, y, size[0] + 4 * padding, size[1] + 3 * padding, null);
    }

    /**
     * 绘制文本
     * @param g2d 画板
     * @param width 画板宽度
     * @param text 文字文本
     * @param font 字体样式
     * @param color 字体颜色
     * @param offsetX 文字左上角在画板中的x位置，如果选择对齐方式为靠右，则文字右上角
     * @param offsetY 文字左上角在画板中的y位置，如果选择对齐方式为靠右，则文字右上角
     * @param alignment 文字对齐方式
     * @return 绘制文本的宽度和高度
     */
    private static int[] drawString(Graphics2D g2d, int width, String text,
                                    Font font, Color color, int offsetX, int offsetY, TEXT_ALIGNMENT alignment) {
        // 准备画板
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON); // 抗锯齿
        g2d.setFont(font);
        g2d.setColor(color);

        // 预计算绘制文本宽高度
        FontMetrics metrics = FontDesignMetrics.getMetrics(font);
        int text_width = metrics.stringWidth(text);
        int text_height = metrics.getAscent();

        // 计算绘制位置
        int x = 0;
        int y = offsetY + metrics.getAscent();
        switch (alignment) {
            case LEFT:
                x = offsetX;
                break;
            case CENTER:
                x = (width - text_width) / 2 + offsetX;
                break;
            case RIGHT:
                x = width - text_width - offsetX;
                break;
        }

        g2d.drawString(text, x, y);

        return new int[] { text_width, text_height };
    }
}
