package org.fightjc.xybot.util.genshin;

import org.fightjc.xybot.pojo.genshin.AnnounceBean;
import org.fightjc.xybot.pojo.genshin.AnnounceDto;
import org.fightjc.xybot.util.MessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.font.FontDesignMetrics;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class GenshinAnnounceDrawHelper {

    private static final Logger logger = LoggerFactory.getLogger(GenshinAnnounceDrawHelper.class);

    /**
     * 生成原神日历图片
     * @param list
     * @return
     */
    public static BufferedImage drawAnnounce(List<AnnounceBean> list) {
        //region 预设参数
        int TITLE_RESUME_GAP = 10; // 标题和概要间距
        int RESUME_GAP = 3; // 概要间隔

        Font f_title = new Font(Font.SANS_SERIF, Font.BOLD, 28);
        Font f_resume = new Font("Microsoft YaHei", Font.PLAIN, 28);
        //endregion

        //region 计算画板大小
        int width = 1000;
        int height = 600;
        //endregion

        //region 准备画板
        BufferedImage target = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = target.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // 抗锯齿
        g2d.setBackground(Color.WHITE);
        g2d.clearRect(0, 0, width, height);
        //endregion

        int currY = 0;

        //region 标题
        g2d.setFont(f_title);
        g2d.setColor(Color.BLACK);
        FontMetrics metrics_title = FontDesignMetrics.getMetrics(f_title);
        int text_title_height = metrics_title.getAscent();

        // 左上标题
        String title = "原神日历";
        g2d.drawString(title, 0, currY + text_title_height);

        // 时间
        String time = MessageUtil.getCurrentDate();
        int text_time_width = metrics_title.stringWidth(time);
        g2d.drawString(time, (float) (width - text_time_width) / 2, currY + text_title_height);

        currY += text_title_height + TITLE_RESUME_GAP;
        //endregion

        //region 列表
        g2d.setFont(f_resume);
        FontMetrics metrics_resume = FontDesignMetrics.getMetrics(f_resume);
        int text_resume_ascent = metrics_resume.getAscent();
        int text_resume_height = metrics_resume.getHeight();

        for (int i = 0; i < list.size(); i++) {
            AnnounceBean bean = list.get(i);

            // 背景色
            switch (bean.getType()) {
                case abyss:
                case award:
                    g2d.setColor(Color.WHITE);
                    g2d.setBackground(new Color(138, 43,226)); // 8A2BE2
                    break;
                case gacha:
                    g2d.setColor(Color.WHITE);
                    g2d.setBackground(new Color(255, 140, 0)); // FF8C00
                    break;
                case event:
                    g2d.setColor(Color.WHITE);
                    g2d.setBackground(new Color(34, 139, 34)); // 228B22
                    break;
                case other:
                    g2d.setColor(Color.BLACK);
                    g2d.setBackground(Color.WHITE);
                    break;
            }
            g2d.clearRect(0, currY, width, text_resume_height);

            // 概要
            String resume = bean.getTitle();
            g2d.drawString(resume, 0, currY + text_resume_ascent);

            // 结束时间
            String deadline = bean.getDeadLineDescription();
            int text_deadline_width = metrics_resume.stringWidth(deadline);
            g2d.drawString(deadline, width - text_deadline_width, currY + text_resume_ascent);

            currY += text_resume_height + RESUME_GAP;
        }
        //endregion

        //region 释放资源
        g2d.dispose();
        //endregion

        return target;
    }
}
