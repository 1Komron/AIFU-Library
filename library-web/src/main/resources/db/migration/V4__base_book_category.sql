CREATE TABLE base_book_category (
                                    id SERIAL PRIMARY KEY,
                                    name VARCHAR(255) NOT NULL,
                                    is_deleted BOOLEAN DEFAULT FALSE
);
