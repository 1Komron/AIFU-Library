CREATE TABLE book_copy (
                           id SERIAL PRIMARY KEY,
                           inventory_number VARCHAR(255) NOT NULL,
                           epc VARCHAR(255) NOT NULL,
                           shelf_location VARCHAR(255),
                           notes TEXT,
                           is_taken BOOLEAN DEFAULT FALSE,
                           is_deleted BOOLEAN DEFAULT FALSE,
                           base_book_id INT,
                           CONSTRAINT fk_bookcopy_base_book FOREIGN KEY (base_book_id) REFERENCES base_book(id)
);

CREATE INDEX idx_epc ON book_copy(epc);
