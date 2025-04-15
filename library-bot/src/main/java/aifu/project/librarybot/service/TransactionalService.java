package aifu.project.librarybot.service;

import aifu.project.librarybot.enums.TransactionStep;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class TransactionalService {

    private final ConcurrentHashMap<Long, TransactionStep> transactionStates = new ConcurrentHashMap<>();

    public TransactionStep getState(Long chatId){
        return transactionStates.getOrDefault(chatId, null);
    }

    public void putState(Long chatId, TransactionStep step){
        transactionStates.put(chatId, step);
    }

    public void clearState(Long chatId){
        transactionStates.remove(chatId);
    }
}
