package org.fightjc.xybot.annotate;

import org.fightjc.xybot.command.BaseCommand;
import org.fightjc.xybot.util.BotSwitch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class AnnotateAnalyzer implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(AnnotateAnalyzer.class);

    // 需要注册的指令
    List<BaseCommand> commands = new ArrayList<>();

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() == null) {
            logger.info("开始注册指令注解");
            Map<String, Object> commandBeans = event.getApplicationContext().getBeansWithAnnotation(CommandAnnotate.class);
            for (Object bean : commandBeans.values()) {
                if (bean instanceof BaseCommand) {
                    CommandAnnotate commandAnnotate = bean.getClass().getAnnotation(CommandAnnotate.class);
                    if (commandAnnotate.autoLoad()) {
                        commands.add((BaseCommand) bean);
                    }
                }
            }

            logger.info("开始注册开关注解");
            Map<String, Object> switchBeans = event.getApplicationContext().getBeansWithAnnotation(SwitchAnnotate.class);
            for (Object bean: switchBeans.values()) {
                if (bean instanceof BaseCommand) {
                    SwitchAnnotate switchAnnotate = bean.getClass().getAnnotation(SwitchAnnotate.class);
                    BotSwitch.getInstance().registerSwitch(switchAnnotate.name(), switchAnnotate.autoOn());
                }
            }
        }
    }

    public List<BaseCommand> getCommands() {
        return commands;
    }
}
