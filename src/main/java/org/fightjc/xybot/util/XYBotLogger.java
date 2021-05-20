package org.fightjc.xybot.util;

import net.mamoe.mirai.utils.MiraiLoggerPlatformBase;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description: 用spring的slf4j实现mirai的log接口,让bot层的log重定向到spring log下
 */
public class XYBotLogger extends MiraiLoggerPlatformBase {

    private static final Logger logger = LoggerFactory.getLogger("XYBot");

    @Override
    protected void debug0(@Nullable String s, @Nullable Throwable throwable) {
        logger.debug(s, throwable);
    }

    @Override
    protected void error0(@Nullable String s, @Nullable Throwable throwable) {
        logger.error(s, throwable);
    }

    @Override
    protected void info0(@Nullable String s, @Nullable Throwable throwable) {
        logger.info(s, throwable);
    }

    @Override
    protected void verbose0(@Nullable String s, @Nullable Throwable throwable) {
        logger.info(s, throwable);
    }

    @Override
    protected void warning0(@Nullable String s, @Nullable Throwable throwable) {
        logger.warn(s, throwable);
    }

    @Nullable
    @Override
    public String getIdentity() {
        return "XYBot";
    }
}
