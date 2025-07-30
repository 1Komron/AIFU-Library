package aifu.project.librarybot.service;

import aifu.project.common_domain.entity.BaseBook;
import aifu.project.common_domain.entity.BaseBookCategory;
import aifu.project.common_domain.exceptions.BaseBookCategoryNotFoundException;
import aifu.project.common_domain.dto.BookPartList;
import aifu.project.librarybot.repository.BaseBookRepository;
import aifu.project.librarybot.utils.KeyboardUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {
    private final BaseBookRepository bookRepository;
    private final BaseBookCategoryService categoryService;

    public BookPartList getBookList(String categoryId, int page) {
        BaseBookCategory category;
        try {
            category = categoryService.getCategory(Integer.valueOf(categoryId))
                    .orElseThrow(() -> new BaseBookCategoryNotFoundException(Integer.valueOf(categoryId)));
        } catch (BaseBookCategoryNotFoundException e) {
            log.error("BaseBookCategory not found. Message: {}", e.getMessage());
            throw e;
        }

        Pageable pageable = PageRequest.of(--page, 5);

        Page<BaseBook> books = bookRepository.findByCategoryAndIsDeletedFalse(category, pageable);

        return getBookPartList(books);
    }

    private BookPartList getBookPartList(Page<BaseBook> books) {
        List<BaseBook> bookList = books.getContent();

        InlineKeyboardMarkup selectButtons = KeyboardUtil.getBookSelectButtons(bookList);

        String list = getBookList(bookList);

        return new BookPartList(list, books.getNumber() + 1, books.getTotalPages(), selectButtons);
    }

    private String getBookList(List<BaseBook> books) {
        StringBuilder sb = new StringBuilder();

        int count = 1;
        for (BaseBook book : books) {
            sb.append(count)
                    .append(". ")
                    .append(book.getAuthor())
                    .append(" - ")
                    .append(book.getTitle())
                    .append("\n");
            count++;
        }

        return sb.toString();
    }
}
