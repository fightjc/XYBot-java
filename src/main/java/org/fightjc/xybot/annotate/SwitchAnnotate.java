package org.fightjc.xybot.annotate;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SwitchAnnotate {

    /**
     * 登记的开关名称
     * @return
     */
    String name();

    /**
     * 是否自动开启 默认关闭
     * @return
     */
    boolean autoOn() default false;
}
