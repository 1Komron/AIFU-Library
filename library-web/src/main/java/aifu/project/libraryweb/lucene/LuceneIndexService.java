package aifu.project.libraryweb.lucene;

import aifu.project.common_domain.entity.BaseBook;
import aifu.project.common_domain.entity.PdfBook;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;

@Slf4j
@Service
public class LuceneIndexService {
    private final CustomAnalyzer analyzer = new CustomAnalyzer();

    private FSDirectory getDirectory(String type) throws IOException {
        return FSDirectory.open(Path.of("lucene-index/" + type));
    }

    public void indexBaseBooks(BaseBook book) throws IOException {
        try (var writer = new IndexWriter(getDirectory("regular"), new IndexWriterConfig(analyzer))) {
            Document doc = createDocument(String.valueOf(book.getId()), book.getAuthor(), book.getTitle());
            writer.updateDocument(new Term("id", String.valueOf(book.getId())), doc);
            writer.commit();

            log.info("Index created successfully. BaseBook id {}", book.getId());
        }
    }

    public void indexPdfBooks(PdfBook book) throws IOException {
        try (var writer = new IndexWriter(getDirectory("electronic"), new IndexWriterConfig(analyzer))) {
            Document doc = createDocument(String.valueOf(book.getId()), book.getAuthor(), book.getTitle());
            writer.updateDocument(new Term("id", String.valueOf(book.getId())), doc);
            writer.commit();
        }
    }

    private Document createDocument(String id, String author, String title) {
        Document doc = new Document();
        doc.add(new StringField("id", String.valueOf(id), Field.Store.YES));
        doc.add(new TextField("author", author, Field.Store.YES));
        doc.add(new TextField("title", title, Field.Store.YES));

        return doc;
    }

    public void deleteBaseBookIndex(Integer id) throws IOException {
        try (var writer = new IndexWriter(getDirectory("regular"), new IndexWriterConfig(analyzer))) {
            writer.deleteDocuments(new Term("id", String.valueOf(id)));
            writer.commit();

            log.info("Index deleted successfully by BaseBook id {}", id);
        }
    }

    public void deletePDFBookIndex(Integer id) throws IOException {
        try (var writer = new IndexWriter(getDirectory("electronic"), new IndexWriterConfig(analyzer))) {
            writer.deleteDocuments(new Term("id", String.valueOf(id)));
            writer.commit();

            log.info("Index deleted successfully. PDF Book id {}", id);
        }
    }
}
