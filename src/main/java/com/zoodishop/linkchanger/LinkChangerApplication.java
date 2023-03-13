package com.zoodishop.linkchanger;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class LinkChangerApplication {

    public static void main(String[] args) {
        SpringApplication.run(LinkChangerApplication.class, args);
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(new LinkChangerClass());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Bean
    public PropertiesFactoryBean bot() {
        PropertiesFactoryBean factoryBean = new PropertiesFactoryBean();
        factoryBean.setFileEncoding("UTF-8");
        factoryBean.setLocation(new ClassPathResource("bot.properties"));
        return factoryBean;
    }

}
