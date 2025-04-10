package aifu.project.librarybot;

import aifu.project.librarybot.config.TelegramBot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
@EntityScan("aifu.project.commondomain.entity")
@EnableJpaRepositories("aifu.project.commondomain.repository")
@ComponentScan({"aifu.project.librarybot", "aifu.project.commondomain"})
public class LibraryBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryBotApplication.class, args);
    }

    @Bean
    public TelegramBotsApi telegramBotsApi(TelegramBot telegramBot) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(telegramBot);
        return botsApi;
    }

    @Bean
    DefaultBotOptions botOptions() {
        return new DefaultBotOptions();
    }
}
