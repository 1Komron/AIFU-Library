package aifu.project.librarybot.service;

import aifu.project.commondomain.entity.BaseBook;
import aifu.project.commondomain.entity.BaseBookCategory;
import aifu.project.commondomain.payload.PartList;
import aifu.project.commondomain.payload.ResponseMessage;
import aifu.project.librarybot.repository.BaseBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BaseBookRepository bookRepository;
    private final BaseBookCategoryService categoryService;

    public PartList getBookList(String categoryId, int page) {
        BaseBookCategory category = categoryService.getCategory(Integer.valueOf(categoryId))
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Pageable pageable = PageRequest.of(--page, 1);

        Page<BaseBook> books = bookRepository.findByCategory(category, pageable);

        return getPartList(books);
    }

    private PartList getPartList(Page<BaseBook> books) {
        List<BaseBook> bookList = books.getContent();

        String list = getBookList(bookList);

        return new PartList(list, books.getNumber() + 1, books.getTotalPages());
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

    public ResponseEntity<ResponseMessage> countBooks() {
        long count = bookRepository.count();
        return ResponseEntity.ok(new ResponseMessage(true, "Book count", count));
    }
}
