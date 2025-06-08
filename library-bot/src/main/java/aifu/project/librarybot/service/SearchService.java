package aifu.project.librarybot.service;

import aifu.project.commondomain.dto.SearchDTO;
import aifu.project.commondomain.dto.SearchPart;
import aifu.project.commondomain.entity.BaseBook;
import aifu.project.commondomain.entity.BookCopy;
import aifu.project.commondomain.payload.PartList;
import aifu.project.librarybot.lucene.LuceneSearchService;
import aifu.project.librarybot.repository.BaseBookRepository;
import aifu.project.librarybot.utils.ExecuteUtil;
import aifu.project.librarybot.utils.MessageKeys;
import aifu.project.librarybot.utils.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class SearchService {
    private final LuceneSearchService luceneSearchService;
    private final BaseBookRepository baseBookRepository;
    private final ExecuteUtil executeUtil;


    public PartList search(Long chatId, String request, String lang, int page) {
        request = request.split("\\|")[1];
        Optional<List<SearchDTO>> optionalList;
        try {
            optionalList = luceneSearchService.searchBooks(request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (optionalList.isEmpty()) {
            executeUtil.executeMessage(chatId.toString(), MessageKeys.SEARCH_NOT_FOUND, lang);
            return null;
        }

        SearchPart searchPart = customPageable(optionalList.get(), page);
        return getSearchResult(searchPart, chatId, lang);
    }

    private SearchPart customPageable(List<SearchDTO> books, int page) {
        --page;
        int pageSize = 3;
        int size = books.size();

        int totalPages = (int) Math.ceil((double) size / pageSize);


        if (page < 0 || page >= totalPages) {
            throw new RuntimeException("Page must be between 0 and " + (totalPages - 1));
        }

        int fromIndex = page * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, size);

        return new SearchPart(books.subList(fromIndex, toIndex), ++page, totalPages);
    }

    @SneakyThrows
    public PartList getSearchResult(SearchPart part, Long chatId, String lang) {
        List<Integer> bookIds = part.searchDTOs().stream()
                .map(SearchDTO::id)
                .toList();

        List<BaseBook> baseBookList = baseBookRepository.findByIdIn(bookIds);
        StringBuilder sb = new StringBuilder();
        baseBookList.forEach(book -> {
            List<BookCopy> copies = book.getCopies();
            int notTakenCopies = getNotTakenCopies(copies);

            String template = MessageUtil.get(MessageKeys.SEARCH_BOOK, lang);
            String formatted = template.formatted(book.getId(), book.getAuthor(), book.getTitle(),
                    book.getIsbn(), book.getCategory().getName(), book.getPublicationYear(),
                    book.getLanguage(), book.getTitleDetails(),
                    copies.size(), notTakenCopies);

            sb.append(formatted).append("\n");
        });
        return new PartList(sb.toString(), part.currentPage(), part.totalPages());
    }

    private int getNotTakenCopies(List<BookCopy> copies) {
        int takenCopies = 0;

        for (BookCopy copy : copies) {
            if (!copy.isTaken())
                takenCopies++;

        }
        return takenCopies;
    }
}
