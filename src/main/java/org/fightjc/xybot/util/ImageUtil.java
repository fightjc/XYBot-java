package org.fightjc.xybot.util;

import net.mamoe.mirai.utils.ExternalResource;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ImageUtil {

    /**
     * 将 BufferedImage 格式的图片转为 mirai 可识别图片资源
     * @param image
     * @return
     * @throws Exception
     */
    public static ExternalResource bufferedImage2ExternalResource(BufferedImage image) throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", stream);

        return ExternalResource.create(new ByteArrayInputStream(stream.toByteArray()));
    }

    /**
     * 获取网络图片
     * @param uri
     * @return
     */
    public static BufferedImage getImageFromUri(String uri) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(uri);
        try {
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);

            HttpEntity entity = httpResponse.getEntity();
            InputStream inStream = entity.getContent();
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            byte[] data = outStream.toByteArray();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            BufferedImage image = ImageIO.read(in);

            EntityUtils.consume(entity);
            outStream.close();
            httpResponse.close();

            return image;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    /**
     * 缩放图片
     * @param image 原图
     * @param newWidth 生成宽度
     * @param newHeight 生成高度
     * @return 缩放后图片对象
     */
    public static BufferedImage getScaledImage(BufferedImage image, int newWidth, int newHeight) {
        int width = image.getWidth();
        int height = image.getHeight();

        if (width == newWidth && height == newHeight) return image;  // 不需要缩放图片，减少操作

        // 计算缩放比例
        double xScale = (double) newWidth / width;
        double yScale = (double) newHeight / height;

        BufferedImage scaledImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D scaleImageG2d = scaledImage.createGraphics();
        AffineTransform at = AffineTransform.getScaleInstance(xScale, yScale);
        scaleImageG2d.drawRenderedImage(image, at);
        scaleImageG2d.dispose();

        return scaledImage;
    }
}
