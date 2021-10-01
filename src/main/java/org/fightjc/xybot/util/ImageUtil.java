package org.fightjc.xybot.util;

import com.alibaba.fastjson.JSONObject;
import net.mamoe.mirai.utils.ExternalResource;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.fightjc.xybot.po.HttpClientResult;

import javax.imageio.ImageIO;
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
     * 获取网络图片并转为 mirai 可识别图片资源
     * @param uri
     * @return
     */
    public static ExternalResource getImageFromUri(String uri) {
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
            ExternalResource image = ExternalResource.create(new ByteArrayInputStream(data));

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
}
