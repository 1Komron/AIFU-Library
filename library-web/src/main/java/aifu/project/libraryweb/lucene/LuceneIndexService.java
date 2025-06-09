package aifu.project.libraryweb.lucene;

import aifu.project.common_domain.entity.BaseBook;
import aifu.project.common_domain.entity.PdfBook;
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
}
