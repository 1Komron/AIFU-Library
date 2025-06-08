package aifu.project.librarybot.utils;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
public class ExecuteUtil extends DefaultAbsSender {

    public ExecuteUtil(DefaultBotOptions options,
                       @Value("${bot.token}") String botToken) {
        super(options, botToken);
    }

    @SneakyThrows
    public void executeMessage(String chatId, String messageKeys, String lang) {
        SendMessage message = MessageUtil.createMessage(chatId, MessageUtil.get(messageKeys, lang));
        execute(message);
    }

    @SneakyThrows
    public void answerCallback(String id){
        AnswerCallbackQuery answer = new AnswerCallbackQuery();
        answer.setCallbackQueryId(id);

        execute(answer);
    }
}
