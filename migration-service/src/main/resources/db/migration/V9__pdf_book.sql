CREATE TABLE pdf_book (
                          id SERIAL PRIMARY KEY,
                          size DOUBLE PRECISION,
                          author VARCHAR(255),
                          title VARCHAR(255),
                          publication_year INT,
                          category_id INT,
                          pdf_url VARCHAR(255),
                          image_url VARCHAR(255),
                          isbn VARCHAR(50),
                          page_count INT,
                          publisher VARCHAR(255),
                          language VARCHAR(50),
                          script VARCHAR(50),
                          description TEXT,
                          local_date DATE DEFAULT CURRENT_DATE,
                          CONSTRAINT fk_pdfbook_category FOREIGN KEY (category_id) REFERENCES category(id)
);
