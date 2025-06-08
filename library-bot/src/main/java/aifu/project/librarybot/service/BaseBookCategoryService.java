package aifu.project.librarybot.service;

import aifu.project.commondomain.entity.BaseBookCategory;
import aifu.project.librarybot.repository.BaseBookCategoryRepository;
import aifu.project.librarybot.utils.KeyboardUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BaseBookCategoryService {
    private final BaseBookCategoryRepository repository;

    public InlineKeyboardMarkup getCategoryPartList(int page, String lang) {
        Pageable pageable = PageRequest.of(--page, 6);
        Page<BaseBookCategory> part = repository.findAll(pageable);

        HashMap<Integer, String> map = new HashMap<>();
        part.getContent().forEach(category -> map.put(category.getId(), category.getName()));

        return getInlineKeyboard(map, lang, ++page, part.getTotalPages());
    }

    private InlineKeyboardMarkup getInlineKeyboard(HashMap<Integer, String> list, String lang, int page, int totalPages) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardMarkup categoryButtons = KeyboardUtil.getCategoryListInlineButtons(list);
        InlineKeyboardMarkup controlButtons = KeyboardUtil.controlInlineKeyboard(lang, page, totalPages, "list");

        List<List<InlineKeyboardButton>> categoryKeyboard = categoryButtons.getKeyboard();
        List<List<InlineKeyboardButton>> controlKeyboard = (controlButtons != null) ? controlButtons.getKeyboard() : null;

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>(categoryKeyboard);

        if (controlKeyboard != null) {
            buttons.addAll(controlKeyboard);
        }

        inlineKeyboardMarkup.setKeyboard(buttons);
        return inlineKeyboardMarkup;
    }

    public Optional<BaseBookCategory> getCategory(Integer id) {
      return repository.findById(id);
    }
}
