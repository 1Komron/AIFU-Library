package aifu.project.libraryweb.runner;

import aifu.project.common_domain.entity.BaseBook;
import aifu.project.libraryweb.lucene.CustomAnalyzer;
import aifu.project.libraryweb.repository.BaseBookRepository;
import lombok.RequiredArgsConstructor;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.FSDirectory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Runner implements CommandLineRunner {
    private final BaseBookRepository repository;
    private final CustomAnalyzer analyzer;

    @Override
    public void run(String... args) throws Exception {
        List<BaseBook> books = repository.findAll();

        try (FSDirectory directory = FSDirectory.open(Path.of("lucene-index/regular"));
             IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(analyzer))) {

            for (BaseBook book : books) {
                Document doc = new Document();
                doc.add(new StringField("id", String.valueOf(book.getId()), Field.Store.YES));
                doc.add(new TextField("author", book.getAuthor(), Field.Store.YES));
                doc.add(new TextField("title", book.getTitle(), Field.Store.YES));
                writer.updateDocument(new Term("id", String.valueOf(book.getId())),doc);
            }

            writer.commit();
        }
    }
}
