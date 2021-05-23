package org.fightjc.xybot.annotate;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Command专用注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Component
public @interface CommandAnnotate {

    /**
     * 是否自动加载 默认加载
     * @return
     */
    boolean autoLoad() default true;
}
