package aifu.project.libraryweb.lucene;

import aifu.project.common_domain.dto.SearchDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParserBase;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LuceneSearchService {

    private final CustomAnalyzer analyzer;

    private FSDirectory getDirectory(String type) throws IOException {
        return FSDirectory.open(Path.of("lucene-index/" + type));
    }

    public Optional<List<SearchDTO>> searchBooks(String queryStr, String type) throws Exception {
        String layout = KeyboardLayoutCorrector.correctLayout(queryStr);
        String layoutReverse = KeyboardLayoutCorrector.correctLayoutReverse(queryStr);

        String transliteratedToRussian = KeyboardLayoutCorrector.transliterateToRussian(queryStr);
        String transliteratedToEnglish = KeyboardLayoutCorrector.transliterateToEnglish(queryStr);

        String correctedToRussian = KeyboardLayoutCorrector.transliterateToRussian(layoutReverse);
        String correctedToEnglish = KeyboardLayoutCorrector.transliterateToEnglish(layout);

        MultiFieldQueryParser parser = new MultiFieldQueryParser(
                new String[]{"title", "author"}, analyzer
        );

        BooleanQuery.Builder finalQuery = new BooleanQuery.Builder();


        for (String value : List.of(layout, layoutReverse,
                correctedToRussian, correctedToEnglish,
                transliteratedToRussian, transliteratedToEnglish,
                queryStr)) {
            try {
                Query query = parser.parse(QueryParserBase.escape(value) + "~");
                finalQuery.add(query, BooleanClause.Occur.SHOULD);
            } catch (ParseException e) {
                log.error("Parsing exception: {} — {}", value, e.getMessage());
            }
        }

        try (DirectoryReader reader = DirectoryReader.open(getDirectory(type))) {
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs topDocs = searcher.search(finalQuery.build(), 20);

            List<Document> results = new ArrayList<>();
            for (ScoreDoc doc : topDocs.scoreDocs) {
                results.add(searcher.storedFields().document(doc.doc));
            }

            if (results.isEmpty())
                return Optional.empty();

            List<SearchDTO> list = results.stream()
                    .map(doc -> new SearchDTO(
                            Integer.valueOf(doc.get("id")),
                            doc.get("author"),
                            doc.get("title")))
                    .toList();

            return Optional.of(list);
        }
    }
}
