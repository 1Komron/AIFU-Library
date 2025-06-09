package aifu.project.libraryweb.controller;

import aifu.project.common_domain.dto.SearchDTO;
import aifu.project.libraryweb.lucene.LuceneIndexService;
import aifu.project.libraryweb.lucene.LuceneSearchService;
import aifu.project.libraryweb.repository.BaseBookRepository;
import aifu.project.libraryweb.repository.PdfBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lucene")
@RequiredArgsConstructor
public class LuceneController {

    private final LuceneIndexService indexService;
    private final LuceneSearchService searchService;

    // Примерные заглушки, замените на реальные источники
    private final BaseBookRepository baseBookRepository;
    private final PdfBookRepository pdfBookRepository;

//    @PostMapping("/index")
//    public String indexAllBooks() throws IOException {
//        indexService.indexBaseBooks(baseBookRepository.findAll());
//        indexService.indexPdfBooks(pdfBookRepository.findAll());
//        return "Индексация завершена!";
//    }

    @GetMapping("/search")
    public List<SearchDTO> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "regular") String type
    ) throws Exception {
       return searchService.searchBooks(query, type).get();
    }

  }
