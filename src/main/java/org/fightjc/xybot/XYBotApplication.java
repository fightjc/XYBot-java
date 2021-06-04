package org.fightjc.xybot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class XYBotApplication {
    public static void main(String[] args) {
        SpringApplication.run(XYBotApplication.class, args);
    }
}
