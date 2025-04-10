package aifu.project.librarybot.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;

@Service
public class ExecuteUtil extends DefaultAbsSender {

    public ExecuteUtil(DefaultBotOptions options,
                       @Value("${bot.token}") String botToken) {
        super(options, botToken);
    }
}
