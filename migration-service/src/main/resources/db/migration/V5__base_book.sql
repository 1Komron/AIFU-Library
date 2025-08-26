CREATE TABLE base_book (
                           id SERIAL PRIMARY KEY,
                           author VARCHAR(255),
                           title VARCHAR(255),
                           series VARCHAR(255),
                           title_details VARCHAR(255),
                           publication_year INT,
                           publisher VARCHAR(255),
                           publication_city VARCHAR(255),
                           isbn VARCHAR(50),
                           page_count INT,
                           language VARCHAR(50),
                           udc VARCHAR(50),
                           is_deleted BOOLEAN DEFAULT FALSE,
                           category_id INT NOT NULL,
                           CONSTRAINT fk_base_book_category FOREIGN KEY (category_id) REFERENCES base_book_category(id)
);
