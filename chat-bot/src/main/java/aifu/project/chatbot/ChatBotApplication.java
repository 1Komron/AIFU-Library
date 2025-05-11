package aifu.project.chatbot;

import aifu.project.chatbot.config.TelegramBot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
@EntityScan("aifu.project.commondomain.entity.test")
@EnableJpaRepositories("aifu.project.commondomain.repository")
@ComponentScan({"aifu.project.chatbot", "aifu.project.commondomain"})
@EnableTransactionManagement
public class ChatBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatBotApplication.class, args);
    }

    @Bean
    public TelegramBotsApi telegramBotsApi(TelegramBot  telegramBot) throws TelegramApiException {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(telegramBot);
        return api;
    }

    @Bean
    DefaultBotOptions defaultBotOptions() {
        return new DefaultBotOptions();
    }

}
