package org.fightjc.xybot.util;

import net.mamoe.mirai.utils.ExternalResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class ImageUtil {

    public static ExternalResource bufferedImage2ExternalResource(BufferedImage image) throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", stream);

        return ExternalResource.create(new ByteArrayInputStream(stream.toByteArray()));
    }
}
