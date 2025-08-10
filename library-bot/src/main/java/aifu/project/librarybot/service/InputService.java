package aifu.project.librarybot.service;

import aifu.project.librarybot.entity.enums.InputStep;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class InputService {

    private final ConcurrentHashMap<Long, InputStep> transactionStates = new ConcurrentHashMap<>();

    public InputStep getState(Long chatId){
        return transactionStates.getOrDefault(chatId, null);
    }

    public void putState(Long chatId, InputStep step){
        transactionStates.put(chatId, step);
    }

    public void clearState(Long chatId){
        transactionStates.remove(chatId);
    }
}
